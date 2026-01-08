package by.vppolkhovsky.tests_app.services;

import by.vppolkhovsky.tests_app.dto.AnswerDto;
import by.vppolkhovsky.tests_app.dto.QuestionDto;
import by.vppolkhovsky.tests_app.dto.QuizContext;
import by.vppolkhovsky.tests_app.dto.UserDto;
import by.vppolkhovsky.tests_app.dto.ws.*;
import by.vppolkhovsky.tests_app.events.SaveAnswerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private static final Integer ROUND_TIMER = 30;

    private final SimpMessagingTemplate messagingTemplate;
    private final QuizContextHolder quizContextHolder;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void startGame(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        context.setGameStarted(true);

        notify(contextId, new WsMessage(QuizMessageType.START_GAME));

        startRound(contextId);
    }

    public void startRound(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        context.setRoundStarted(true);

        context.getCurrentQuestion().ifPresentOrElse(question -> {
            int index = context.getQuestionOrder().indexOf(question.getId());
            context.setCurrentRound(index + 2);
            context.setCurrentQuestionId(context.getQuestionOrder().get(index + 1));
        }, () -> {
            context.setCurrentRound(1);
            context.setCurrentQuestionId(context.getQuestionOrder().getFirst());
        });

        WsShowQuestionMessage showNewQuestion = WsShowQuestionMessage.builder()
            .currentRound(context.getCurrentRound())
            .questionId(context.getCurrentQuestionId())
            .roundTime(ROUND_TIMER)
            .build();

        notify(contextId, new WsMessage(QuizMessageType.START_ROUND));
        notify(contextId, showNewQuestion);

        ScheduledFuture<?> stopRoundTask = executor.schedule(() -> stopRound(contextId), ROUND_TIMER, TimeUnit.SECONDS);
        context.setStopRoundTask(stopRoundTask);
    }

    public void stopRound(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        context.setRoundStarted(false);

        notify(contextId, new WsMessage(QuizMessageType.STOP_ROUND));
        notify(contextId, new WsMessage(QuizMessageType.SHOW_QUESTION_ANSWER));

        context.getPreviousQuestion().map(QuestionDto::getId).ifPresentOrElse(id -> {
            executor.schedule(() -> notify(contextId, new WsRatingMessage(QuizMessageType.SHOW_RATING, id, context.getQuestionToUserToRating().get(id))), 5, TimeUnit.SECONDS);
        }, () -> {
            Set<UserDto> participants = context.getParticipants();
            Map<String, Integer> firstResult = participants.stream().collect(Collectors.toMap(UserDto::getUserId, dto -> 0));
            executor.schedule(() -> notify(contextId, new WsRatingMessage(QuizMessageType.SHOW_RATING, context.getCurrentQuestionId(), firstResult)), 5, TimeUnit.SECONDS);
        });

        executor.schedule(() -> computeRoundResults(contextId, context.getCurrentQuestionId()), 10, TimeUnit.SECONDS);

        if (context.getQuestionOrder().getLast() == context.getCurrentQuestionId()) {
            stopGame(contextId);
        }
    }

    private void computeRoundResults(String contextId, UUID questionId) {
        QuizContext context = quizContextHolder.getContext(contextId);

        List<SaveAnswerEvent> savedAnswers = context.getSavedAnswersQueue().stream()
            .filter(saved -> saved.getQuestionId().equals(questionId))
            .sorted(Comparator.comparing(SaveAnswerEvent::getSavedAt).reversed())
            .toList();

        Set<String> userAnswerSaved = new HashSet<>();
        List<SaveAnswerEvent> lastAnswers = savedAnswers.stream().filter(answer -> userAnswerSaved.add(answer.getUserId()))
            .toList();

        lastAnswers.forEach(saved -> {
            context.getQuestionToUserToAnswer().computeIfAbsent(questionId, uuid -> new HashMap<>())
                .put(saved.getUserId(), saved.getAnswerId());
            context.getQuestionToUserToAnswerTime().computeIfAbsent(questionId, uuid -> new HashMap<>())
                .put(saved.getUserId(), saved.getSavedAt());
        });

        record CorrectAnswer(String userId, LocalDateTime time) {
        }

        UUID correctAnswerId = context.getCurrentQuestionCorrectAnswer().map(AnswerDto::getId).orElse(null);
        List<CorrectAnswer> correctAnswers = context.getQuestionToUserToAnswer().getOrDefault(questionId, Map.of()).entrySet().stream()
            .filter(entry -> entry.getValue().equals(correctAnswerId))
            .map(entry -> new CorrectAnswer(entry.getKey(), context.getQuestionToUserToAnswerTime().get(questionId).get(entry.getKey())))
            .sorted(Comparator.comparing(CorrectAnswer::time).reversed())
            .toList();

        context.getParticipants().forEach(u -> {
            context.getQuestionToUserToRating().computeIfAbsent(questionId, uuid -> new HashMap<>()).put(u.getUserId(), 0);
        });

        Optional<QuestionDto> previousQuestion = context.getPreviousQuestion();
        if (previousQuestion.isPresent()) {
            UUID previousQuestionId = previousQuestion.get().getId();
            context.getQuestionToUserToRating().get(previousQuestionId).forEach((userId, value) -> {
                context.getQuestionToUserToRating().computeIfAbsent(questionId, uuid -> new HashMap<>()).put(userId, value);
            });
        }

        correctAnswers.forEach(correctAnswer -> {
            double halfPrice = context.getCurrentQuestion().get().getPrice() / 2.0;
            long timeDiffMs = Math.abs(Duration.between(correctAnswers.getFirst().time(), correctAnswer.time()).toMillis());
            double timeDiffSeconds = timeDiffMs / 1000.0;
            double exponent = Math.exp(-timeDiffSeconds / 10.0);
            double points = Math.max(halfPrice, halfPrice + halfPrice * exponent);

            if (previousQuestion.isPresent()) {
                UUID previousQuestionId = previousQuestion.get().getId();
                Integer previousResults = context.getQuestionToUserToRating().get(previousQuestionId).get(correctAnswer.userId());
                points = points + Optional.ofNullable(previousResults).orElse(0);
            }

            context.getQuestionToUserToRating().computeIfAbsent(questionId, uuid -> new HashMap<>()).put(correctAnswer.userId(), (int) Math.round(points));
        });

        context.getSavedAnswersQueue().clear();
        context.getSavedAnswers().clear();

        notify(contextId, new WsRatingMessage(QuizMessageType.SHOW_UPDATED_RATING, questionId, context.getQuestionToUserToRating().get(questionId)));
    }

    public void stopGame(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        context.setGameFinished(true);

        notify(contextId, new WsMessage(QuizMessageType.STOP_GAME));
    }

    public void connectParticipant(String contextId, UserDto dto) {
        if (quizContextHolder.getContext(contextId).getParticipants().add(dto)) {
            WsUserConnectedMessage wsUserConnectedMessage = new WsUserConnectedMessage(dto);
            notify(contextId, wsUserConnectedMessage);
        }
    }

    public void disconnect(String contextId, String userId) {
        notify(contextId, userId, new WsMessage(QuizMessageType.DISCONNECT));
    }

    @EventListener
    public void listenAnswerSaved(SaveAnswerEvent event) {
        QuizContext context = quizContextHolder.getContext(event.getContextId());
        String username = context.getUserById(event.getUserId()).map(UserDto::getUsername).orElse("<Неопозннай банан>");

        if (!context.getRoundStarted()) {
            return;
        }

        context.getSavedAnswersQueue().add(event);
        context.getSavedAnswers().add(event.getUserId());

        WsAnswerSavedMessage message = WsAnswerSavedMessage.builder()
            .text("%s отправил ответ.".formatted(username))
            .answerId(event.getAnswerId())
            .userId(event.getUserId())
            .questionId(event.getQuestionId())
            .answerId(event.getAnswerId())
            .contextId(event.getContextId())
            .build();

        notify(event.getContextId(), message);
        notify(event.getContextId(), event.getUserId(), message);

        if (context.getSavedAnswers().size() == context.getParticipants().size()) {
            if (context.getStopRoundTask().cancel(false))
                executor.schedule(() -> stopRound(context.getId()), 2, TimeUnit.SECONDS);
        }
    }

    private void notify(String contextId, Object object) {
        messagingTemplate.convertAndSend("/queue/quiz/%s/updates".formatted(contextId), object);
    }

    private void notify(String contextId, String userId, Object object) {
        messagingTemplate.convertAndSend("/queue/quiz/%s/%s/updates".formatted(contextId, userId), object);
    }

}

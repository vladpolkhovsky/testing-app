package by.vppolkhovsky.tests_app.services;

import by.vppolkhovsky.tests_app.dto.*;
import by.vppolkhovsky.tests_app.dto.ws.WsQuizInitMessageDto;
import by.vppolkhovsky.tests_app.dto.ws.WsQuizNewRatingMessageDto;
import by.vppolkhovsky.tests_app.dto.ws.WsQuizShowNewQuestionMessageDto;
import by.vppolkhovsky.tests_app.dto.ws.WsStartStopRoundMessageDto;
import by.vppolkhovsky.tests_app.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private static int TIME_START_DELEY = 1;
    private static int ROUND_TIME = 25;
    private static int DISCUSSION_DELAY = 10;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final SimpMessagingTemplate messagingTemplate;
    private final QuizContextHolder quizContextHolder;
    private final QuizMapper quizMapper;

    public void notifySilent(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        notifyWithInitialMessage(context);
    }

    public void handleUserConnection(String contextId, UserDto dto) {
        QuizContext context = quizContextHolder.getContext(contextId);
        context.getUserRating().computeIfAbsent(dto.getUserId(), key -> UserRatingDto.builder().user(dto).build());
        notifyWithInitialMessage(context);
        log.info("User connected {} : {}", contextId, dto);
    }

    public void startRound(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);

        if (context.getGameStarted() == false) context.setGameStarted(true);
        if (context.getRoundStarted() == false) context.setRoundStarted(true);

        QuestionDto nextQuestion = null;
        if (context.getCurrentQuestionId() == null) {
            nextQuestion = context.getQuiz().getQuestions().getFirst();
        } else {
            QuestionDto currentQuestion = context.getQuiz().getQuestions().stream()
                .filter(q -> Objects.equals(q.getId(), context.getCurrentQuestionId()))
                .findAny().orElseThrow();

            int currentQuestionIndex = context.getQuiz().getQuestions().indexOf(currentQuestion);

            if (currentQuestionIndex + 1 < context.getQuiz().getQuestions().size()) {
                nextQuestion = context.getQuiz().getQuestions().get(currentQuestionIndex + 1);
            }
        }

        if (nextQuestion == null) {
            context.setGameFinished(true);
            context.setRoundStarted(false);
            notifySilent(contextId);
            return;
        }

        context.setCurrentQuestionId(nextQuestion.getId());

        notifyShowMessageMessage(context);

        CompletableFuture.runAsync(() -> {}, CompletableFuture.delayedExecutor(TIME_START_DELEY, TimeUnit.SECONDS, executor))
            .thenRunAsync(() -> notify(contextId, WsStartStopRoundMessageDto.start(context.getCurrentQuestionId(), ROUND_TIME)), executor)
            .thenRunAsync(() -> {}, CompletableFuture.delayedExecutor(ROUND_TIME, TimeUnit.SECONDS, executor))
            .thenRunAsync(() -> {
                WsStartStopRoundMessageDto stopMessage = WsStartStopRoundMessageDto.stop(context.getCurrentQuestionId());

                context.getCurrentQuestion().ifPresent(q -> {
                    stopMessage.setTextAlternative(q.getTextAlternative());
                    stopMessage.setImageAlternativeId(q.getImageAlternativeId());
                });

                notify(contextId, stopMessage);
            }, executor)
            .thenRunAsync(() -> {}, CompletableFuture.delayedExecutor(DISCUSSION_DELAY, TimeUnit.SECONDS, executor))
            .thenRunAsync(() -> stopRound(contextId), executor);

        log.info("Start round {}", contextId);
    }

    public void stopRound(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);

        context.setRoundStarted(false);

        UUID questionId = context.getCurrentQuestionId();

        Optional<QuestionDto> question = context.getQuiz().getQuestions().stream()
            .filter(q -> Objects.equals(q.getId(), questionId))
            .findAny();

        UUID correctAnswerId = question.stream()
            .map(QuestionDto::getAnswers)
            .flatMap(Collection::stream)
            .filter(AnswerDto::getIsCorrect)
            .findFirst()
            .map(AnswerDto::getId)
            .orElse(UUID.randomUUID());

        List<QuizContext.QuestionAnswer> correctAnswers = context.getQuestionAnswers().getOrDefault(questionId, Set.of()).stream()
            .filter(q -> Objects.equals(q.getAnswerId(), correctAnswerId))
            .toList();

        List<LocalDateTime> correctAnswerTimes = correctAnswers.stream()
            .map(QuizContext.QuestionAnswer::getTime)
            .sorted()
            .distinct()
            .toList();

        Random random = new Random();

        correctAnswers.forEach(a -> {
            Optional.ofNullable(context.getUserRating().get(a.getUserId())).ifPresent((rating) -> {
                QuestionDto questionDto = question.orElseThrow();
                int index = correctAnswerTimes.indexOf(a.getTime());
                double multiplier = (correctAnswerTimes.size() - index * 1.0) / correctAnswerTimes.size();
                rating.setRating(rating.getRating() + Math.round((float) (questionDto.getPrice() * multiplier) + random.nextInt(0, 15 + 1)));
            });
        });

        notifyNewRatingMessage(context);

        log.info("Round stopped {}", contextId);
    }

    public void saveAnswer(String contextId, String userId, UUID answerId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        if (context.getRoundStarted()) {
            UUID questionId = context.getCurrentQuestionId();

            QuizContext.QuestionAnswer answer = QuizContext.QuestionAnswer.builder()
                .answerId(answerId)
                .questionId(questionId)
                .userId(userId)
                .time(LocalDateTime.now())
                .build();

            context.getQuestionAnswers().computeIfAbsent(questionId, (k) -> new HashSet<>());
            context.getQuestionAnswers().get(questionId).add(answer);

            log.info("Save answer {}", answer);
        }
    }

    private void notifyWithInitialMessage(QuizContext context) {
        WsQuizInitMessageDto message = quizMapper.toInitMessage(context);
        notify(context.getId(), message);
    }

    private void notifyNewRatingMessage(QuizContext context) {
        WsQuizNewRatingMessageDto message = quizMapper.toNewRating(context);
        notify(context.getId(), message);
    }

    private void notifyShowMessageMessage(QuizContext context) {
        WsQuizShowNewQuestionMessageDto message = quizMapper.toNewQuestionMessage(context);
        notify(context.getId(), message);
    }

    private void notify(String contextId, Object object) {
        log.info("Send update /queue/quiz/{}/updates : {}", contextId, object);
        messagingTemplate.convertAndSend("/queue/quiz/%s/updates".formatted(contextId), object);
    }
}

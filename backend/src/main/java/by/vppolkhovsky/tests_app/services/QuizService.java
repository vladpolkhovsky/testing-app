package by.vppolkhovsky.tests_app.services;

import by.vppolkhovsky.tests_app.dto.*;
import by.vppolkhovsky.tests_app.dto.ws.*;
import by.vppolkhovsky.tests_app.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private static int TIME_START_DELEY = 1;
    private static int ROUND_TIME = 20;
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
            context.setCurrentRound(1);
            nextQuestion = context.getQuiz().getQuestions().getFirst();
        } else {
            QuestionDto currentQuestion = context.getQuiz().getQuestions().stream()
                .filter(q -> Objects.equals(q.getId(), context.getCurrentQuestionId()))
                .findAny().orElseThrow();

            int currentQuestionIndex = context.getQuiz().getQuestions().indexOf(currentQuestion);

            if (currentQuestionIndex + 1 < context.getQuiz().getQuestions().size()) {
                nextQuestion = context.getQuiz().getQuestions().get(currentQuestionIndex + 1);
                context.setCurrentRound(currentQuestionIndex + 2);
            }
        }

        if (nextQuestion == null) {
            context.setGameFinished(true);
            context.setRoundStarted(false);
            context.setCurrentRound(context.getMaxRounds());
            notifySilent(contextId);
            return;
        }

        context.setCurrentQuestionId(nextQuestion.getId());

        notifyShowMessageMessage(context);

        // Запускаем старт раунда через TIME_START_DELEY секунд
        ScheduledFuture<?> startFuture = executor.schedule(() -> {
            notify(contextId, WsStartStopRoundMessageDto.start(
                context.getCurrentQuestionId(),
                ROUND_TIME,
                context.getCurrentRound()
            ));

            // Запускаем таймер остановки раунда через ROUND_TIME секунд
            context.setRoundTimeoutFuture(executor.schedule(() -> sendStopMessage(contextId), ROUND_TIME, TimeUnit.SECONDS));

        }, TIME_START_DELEY, TimeUnit.SECONDS);

        context.setRoundStartFuture(startFuture);

        log.info("Start round scheduled {}", contextId);
    }

    /**
     * Метод для отправки сообщения STOP и запуска discussion таймера
     */
    private void sendStopMessage(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);

        WsStartStopRoundMessageDto stopMessage = WsStartStopRoundMessageDto.stop(
            context.getCurrentQuestionId(),
            context.getCurrentRound(),
            context.getCurrentRound() + 1 > context.getMaxRounds()
        );

        context.getCurrentQuestion().ifPresent(q -> {
            stopMessage.setTextAlternative(StringUtils.trimToNull(q.getTextAlternative()));
            stopMessage.setImageAlternativeId(StringUtils.trimToNull(q.getImageAlternativeId()));
        });

        notify(contextId, stopMessage);

        // Запускаем discussion таймер
        context.setDiscussionFuture(executor.schedule(() -> stopRound(contextId), DISCUSSION_DELAY, TimeUnit.SECONDS));
    }

    /**
     * Метод для принудительной остановки раунда (раньше времени)
     */
    public void triggerStopRound(String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);

        if (context.getRoundTimeoutFuture() != null && !context.getRoundTimeoutFuture().isDone()) {
            context.getRoundTimeoutFuture().cancel(false);
        }

        sendStopMessage(contextId);
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
            .toList();

        correctAnswers.forEach(a -> {
            Optional.ofNullable(context.getUserRating().get(a.getUserId())).ifPresent((rating) -> {
                QuestionDto currentQuestion = question.orElseThrow();
                double halfPrice = currentQuestion.getPrice() / 2.0;

                // Разница в миллисекундах от первого правильного ответа
                long timeDiffMs = Duration.between(correctAnswerTimes.getFirst(), a.getTime()).toMillis();

                // Преобразуем миллисекунды в секунды с точностью до 3 знаков
                double timeDiffSeconds = timeDiffMs / 1000.0;

                // Формула: 500 + 500 * e^(-время/10)
                double exponent = Math.exp(-timeDiffSeconds / 10.0);
                double points = halfPrice + halfPrice * exponent;

                // Гарантируем минимум 500 очков
                points = Math.max(halfPrice, points);

                rating.setRating(rating.getRating() + (int) Math.round(points));
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

            boolean alreadyAdded = context.getQuestionAnswers().get(questionId).contains(answer);

            if (!alreadyAdded) {
                context.getQuestionAnswers().get(questionId).add(answer);

                context.getUserRating().values().stream()
                    .map(UserRatingDto::getUser)
                    .filter(u -> Objects.equals(u.getUserId(), userId))
                    .findAny().ifPresent(user -> {
                        notify(contextId, WsAnswerSavedDto.of(user.getUsername()));
                    });
            }

            log.info("Save answer {}", answer);

            if (context.getQuestionAnswers().get(questionId).size() == context.getUserRating().size()) {
                triggerStopRound(contextId);
            }
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

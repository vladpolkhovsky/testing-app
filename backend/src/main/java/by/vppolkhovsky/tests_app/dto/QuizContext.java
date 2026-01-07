package by.vppolkhovsky.tests_app.dto;

import by.vppolkhovsky.tests_app.events.SaveAnswerEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizContext {

    private String id;
    private QuizDto quiz;

    private Integer currentRound;
    private Integer maxRounds;

    private UUID currentQuestionId;

    @Builder.Default
    private Boolean gameStarted = false;
    @Builder.Default
    private Boolean roundStarted = false;
    @Builder.Default
    private Boolean gameFinished = false;
    @Builder.Default
    private Set<UserDto> participants = new HashSet<>();
    @Builder.Default
    private Map<UUID, Map<String, UUID>> questionToUserToAnswer = new HashMap<>();
    @Builder.Default
    private Map<UUID, Map<String, LocalDateTime>> questionToUserToAnswerTime = new HashMap<>();
    @Builder.Default
    private Map<UUID, Map<String, Integer>> questionToUserToRating = new HashMap<>();
    @Builder.Default
    @JsonIgnore
    private Queue<SaveAnswerEvent> savedAnswersQueue = new ConcurrentLinkedQueue<>();
    @Builder.Default
    @JsonIgnore
    private Set<String> savedAnswers = ConcurrentHashMap.newKeySet();

    @Getter
    @Setter
    @JsonIgnore
    private ScheduledFuture<?> stopRoundTask;

    @JsonIgnore
    public Optional<AnswerDto> getCurrentQuestionCorrectAnswer() {
        return getCurrentQuestion().stream()
            .map(QuestionDto::getAnswers)
            .flatMap(List::stream)
            .filter(AnswerDto::getIsCorrect)
            .findAny();
    }

    @JsonIgnore
    public Optional<QuestionDto> getCurrentQuestion() {
        return getQuiz().getQuestions().stream().filter(q -> q.getId().equals(currentQuestionId))
            .findAny();
    }

    @JsonIgnore
    public Optional<QuestionDto> getPreviousQuestion() {
        int index = getQuestionOrder().indexOf(currentQuestionId);
        if (index <= 0) {
            return Optional.empty();
        }
        return getQuiz().getQuestions().stream().filter(q -> q.getId().equals(getQuestionOrder().get(index - 1)))
            .findAny();
    }

    public List<UUID> getQuestionOrder() {
        return quiz.getQuestions().stream()
            .map(QuestionDto::getId)
            .toList();
    }

    public Optional<UserDto> getUserById(String id) {
        return participants.stream().filter(user -> user.getUserId().equals(id))
            .findAny();
    }
}

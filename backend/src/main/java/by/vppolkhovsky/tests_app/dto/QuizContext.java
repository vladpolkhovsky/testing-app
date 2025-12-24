package by.vppolkhovsky.tests_app.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizContext {

    private String id;
    private QuizDto quiz;

    @Builder.Default
    private Map<UUID, Set<QuestionAnswer>> questionAnswers = new HashMap<>();
    @Builder.Default
    private Map<String, UserRatingDto> userRating = new HashMap<>();

    private UUID currentQuestionId;

    private Integer currentRound;
    private Integer maxRounds;

    @Builder.Default
    private Boolean gameStarted = false;
    @Builder.Default
    private Boolean roundStarted = false;
    @Builder.Default
    private Boolean gameFinished = false;

    public Optional<QuestionDto> getCurrentQuestion() {
        return quiz.getQuestions().stream()
            .filter(q -> Objects.equals(currentQuestionId, q.getId()))
            .findAny();
    }

    public Optional<Integer> getQuestionIndex() {
        return getCurrentQuestion().map(questionDto -> {
            int index = quiz.getQuestions().indexOf(questionDto);
            return index > -1 ? index : null;
        });
    }

    @Data
    @Builder
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class QuestionAnswer {
        @EqualsAndHashCode.Include
        private UUID questionId;
        @EqualsAndHashCode.Include
        private String userId;
        private UUID answerId;
        private LocalDateTime time;
    }
}

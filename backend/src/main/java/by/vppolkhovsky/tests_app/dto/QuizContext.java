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
    private Map<String, Set<QuestionAnswer>> questionAnswers = new HashMap<>();
    @Builder.Default
    private Map<String, UserRatingDto> userRating = new HashMap<>();

    private String currentQuestionId;

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

    @Data
    @Builder
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class QuestionAnswer {
        @EqualsAndHashCode.Include
        private String questionId;
        @EqualsAndHashCode.Include
        private String userId;
        private String answerId;
        private LocalDateTime time;
    }
}

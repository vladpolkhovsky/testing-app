package by.vppolkhovsky.tests_app.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

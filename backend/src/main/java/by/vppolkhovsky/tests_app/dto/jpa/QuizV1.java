package by.vppolkhovsky.tests_app.dto.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JpaVersion(QuizVersion.V1)
public class QuizV1 {
    private UUID id;
    private String title;
    private List<QuestionV1> questions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JpaVersion(QuizVersion.V1)
    public static class QuestionV1 {
        private String text;
        private String textAlternative;
        private UUID imageId;
        private UUID imageAlternativeId;
        private Integer price;
        private List<AnswerV1> answers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JpaVersion(QuizVersion.V1)
    public static class AnswerV1 {
        private String optionText;
        private String optionVariant;
        private String isCorrect;
    }
}

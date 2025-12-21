package by.vppolkhovsky.tests_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private String id;
    private String optionVariant;
    private String optionText;
    private Boolean isCorrect;
}

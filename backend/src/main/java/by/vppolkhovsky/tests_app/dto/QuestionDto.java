package by.vppolkhovsky.tests_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private String id;
    private String text;
    private String imageUrl;
    private String replaceText;
    private String replaceImageUrl;
    private List<AnswerDto> answers;
    private Integer price;
}

package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WsQuizShowNewQuestionMessageDto {
    private QuizMessageType type;
    private WsQuestionDto question;
    private List<WsAnswerOptionDto> answers;
    private String questionId;
    private String nextQuestionId;
}

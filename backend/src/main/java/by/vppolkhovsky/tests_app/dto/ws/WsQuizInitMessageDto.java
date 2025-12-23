package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WsQuizInitMessageDto {
    private QuizMessageType type;
    private String quizId;
    private String title;
    private WsQuestionDto question;
    private List<WsAnswerOptionDto> answers;
    private List<WsRatingItemDto> ratingItems;
    private String questionId;
    private String nextQuestionId;
    private Boolean gameStarted;
    private Boolean roundStarted;
    private Boolean gameFinished;
}

package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class WsShowQuestionMessage extends WsMessage {

    private UUID questionId;
    private Integer currentRound;
    private Integer roundTime;

    public WsShowQuestionMessage(UUID questionId, Integer currentRound, Integer roundTime) {
        super(QuizMessageType.SHOW_QUESTION);
        this.questionId = questionId;
        this.currentRound = currentRound;
        this.roundTime = roundTime;
    }
}

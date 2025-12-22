package by.vppolkhovsky.tests_app.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsStartStopRoundMessageDto {
    private QuizMessageType type;

    @With
    private String replaceImageUrl;
    @With
    private String replaceText;

    private String questionId;
    private Integer duration;

    public static WsStartStopRoundMessageDto start(String questionId, Integer duration) {
        return new WsStartStopRoundMessageDto(QuizMessageType.START_ROUND, null, null, questionId, duration);
    }

    public static WsStartStopRoundMessageDto stop(String questionId) {
        return new WsStartStopRoundMessageDto(QuizMessageType.STOP_ROUND, null, null, questionId, 0);
    }
}

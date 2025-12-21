package by.vppolkhovsky.tests_app.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsStartStopRoundMessageDto {
    private QuizMessageType type;
    private String questionId;
    private Integer duration;

    public static WsStartStopRoundMessageDto start(String questionId, Integer duration) {
        return new WsStartStopRoundMessageDto(QuizMessageType.START_ROUND, questionId, duration);
    }

    public static WsStartStopRoundMessageDto stop(String questionId) {
        return new WsStartStopRoundMessageDto(QuizMessageType.STOP_ROUND, questionId, 0);
    }
}

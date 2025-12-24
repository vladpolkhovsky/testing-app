package by.vppolkhovsky.tests_app.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsStartStopRoundMessageDto {
    private QuizMessageType type;

    @With
    private String imageAlternativeId;
    @With
    private String textAlternative;

    private UUID questionId;
    private Integer duration;

    private Integer nextRound;
    private Boolean gameFinished;

    public static WsStartStopRoundMessageDto start(UUID questionId, Integer duration, Integer currentRound) {
        return new WsStartStopRoundMessageDto(QuizMessageType.START_ROUND, null, null, questionId, duration, currentRound, null);
    }

    public static WsStartStopRoundMessageDto stop(UUID questionId, Integer nextRound, Boolean gameFinished) {
        return new WsStartStopRoundMessageDto(QuizMessageType.STOP_ROUND, null, null, questionId, 0, nextRound, gameFinished);
    }
}

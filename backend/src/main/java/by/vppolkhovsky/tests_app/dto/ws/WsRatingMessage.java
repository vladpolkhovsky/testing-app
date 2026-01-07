package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class WsRatingMessage extends WsMessage {

    private UUID questionId;
    private Map<String, Integer> rating;

    public WsRatingMessage(QuizMessageType type, UUID questionId, Map<String, Integer> rating) {
        super(type);
        this.rating = rating;
        this.questionId = questionId;
    }
}

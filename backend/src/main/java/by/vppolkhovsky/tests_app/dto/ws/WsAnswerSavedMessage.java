package by.vppolkhovsky.tests_app.dto.ws;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class WsAnswerSavedMessage extends WsMessage {

    private String contextId;
    private String userId;

    private UUID questionId;
    private UUID answerId;

    private String text;

    public WsAnswerSavedMessage(String contextId, String userId, UUID questionId, UUID answerId, String text) {
        super(QuizMessageType.NOTIFY_ANSWER_SAVED);
        this.contextId = contextId;
        this.userId = userId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.text = text;
    }
}

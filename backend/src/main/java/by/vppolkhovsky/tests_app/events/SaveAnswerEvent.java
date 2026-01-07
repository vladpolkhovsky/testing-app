package by.vppolkhovsky.tests_app.events;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SaveAnswerEvent {
    private String contextId;
    private String userId;
    private UUID answerId;
    private UUID questionId;
    private LocalDateTime savedAt;
}

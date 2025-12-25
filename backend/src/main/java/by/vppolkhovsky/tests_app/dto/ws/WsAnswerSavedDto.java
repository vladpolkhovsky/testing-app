package by.vppolkhovsky.tests_app.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsAnswerSavedDto {
    private QuizMessageType type;
    private String username;

    public static WsAnswerSavedDto of(String username) {
        return new WsAnswerSavedDto(QuizMessageType.ANSWER_SAVED, username);
    }
}

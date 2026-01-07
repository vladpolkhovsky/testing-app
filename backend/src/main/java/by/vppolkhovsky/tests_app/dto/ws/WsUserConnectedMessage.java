package by.vppolkhovsky.tests_app.dto.ws;

import by.vppolkhovsky.tests_app.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class WsUserConnectedMessage extends WsMessage {

    private UserDto user;

    public WsUserConnectedMessage(UserDto user) {
        super(QuizMessageType.USER_CONNECTED);
        this.user = user;
    }
}

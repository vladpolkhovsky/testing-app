package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WsQuestionDto {
    private String id;
    private String text;
    private String imageUrl;
}

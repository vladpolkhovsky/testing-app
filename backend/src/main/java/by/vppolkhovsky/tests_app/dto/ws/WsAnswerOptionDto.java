package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WsAnswerOptionDto {
    private String id;
    private boolean correct;
    private String optionVariant;
    private String optionText;
}

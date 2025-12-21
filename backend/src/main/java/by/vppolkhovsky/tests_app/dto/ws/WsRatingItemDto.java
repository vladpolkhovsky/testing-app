package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WsRatingItemDto {
    private String userId;
    private String username;
    private Integer rating;
}

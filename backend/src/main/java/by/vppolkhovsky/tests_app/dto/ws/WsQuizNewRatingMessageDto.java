package by.vppolkhovsky.tests_app.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WsQuizNewRatingMessageDto {
    private QuizMessageType type;
    private List<WsRatingItemDto> ratingItems;
}

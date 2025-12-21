package by.vppolkhovsky.tests_app.controller;

import by.vppolkhovsky.tests_app.dto.SaveAnswerDto;
import by.vppolkhovsky.tests_app.dto.UserDto;
import by.vppolkhovsky.tests_app.services.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomWebsocketController {

    private final QuizService quizService;

    // Обработка подключения
    @MessageMapping("/quiz/{roomId}")
    public void userConnection(@DestinationVariable String roomId, @Payload UserDto dto) {
        log.info("/quiz/{} : Payload={}", roomId, dto);
        quizService.handleUserConnection(roomId, dto);
    }

    // Обработка подключения
    @MessageMapping("/quiz/{roomId}/silent")
    public void userConnection(@DestinationVariable String roomId) {
        log.info("/quiz/{}/silent", roomId);
        quizService.notifySilent(roomId);
    }

    // Обработка старта квиза
    @MessageMapping("/quiz/{roomId}/start")
    public void startQuiz(@DestinationVariable String roomId) {
        log.info("/quiz/{}/start", roomId);
        quizService.startRound(roomId);
    }

    // Обработка ответов пользователей
    @MessageMapping("/quiz/{roomId}/{userId}/send")
    public void handleAnswer(@DestinationVariable String roomId,
                             @DestinationVariable String userId,
                             @Payload SaveAnswerDto answerDto) {
        log.info("/quiz/{}/{}/send", roomId, userId);
        quizService.saveAnswer(roomId, userId, answerDto.getAnswerId());
    }
}

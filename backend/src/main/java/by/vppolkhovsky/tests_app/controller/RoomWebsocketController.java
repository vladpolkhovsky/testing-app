package by.vppolkhovsky.tests_app.controller;

import by.vppolkhovsky.tests_app.dto.SaveAnswerDto;
import by.vppolkhovsky.tests_app.dto.UserDto;
import by.vppolkhovsky.tests_app.events.SaveAnswerEvent;
import by.vppolkhovsky.tests_app.services.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomWebsocketController {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final QuizService quizService;

    // Обработка подключения
    @MessageMapping("/quiz/{roomId}")
    public void userConnection(@DestinationVariable String roomId, @Payload UserDto dto) {
        log.info("/quiz/{} : Payload={}", roomId, dto);
        quizService.connectParticipant(roomId, dto);
    }

    // Обработка подключения
    @MessageMapping("/quiz/{roomId}/silent")
    public void userConnection(@DestinationVariable String roomId) {
        log.info("/quiz/{}/silent", roomId);

    }

    // Обработка старта квиза
    @MessageMapping("/quiz/{roomId}/start")
    public void startQuiz(@DestinationVariable String roomId) {
        log.info("/quiz/{}/start", roomId);
        quizService.startGame(roomId);
    }

    // Обработка ответов пользователей
    @MessageMapping("/quiz/{roomId}/{userId}/send")
    public void handleAnswer(@DestinationVariable String roomId,
                             @DestinationVariable String userId,
                             @Payload SaveAnswerDto answerDto) {
        log.info("/quiz/{}/{}/send", roomId, userId);
        applicationEventPublisher.publishEvent(SaveAnswerEvent.builder()
            .userId(userId)
            .contextId(roomId)
            .answerId(answerDto.getAnswerId())
            .questionId(answerDto.getQuestionId())
            .savedAt(LocalDateTime.now())
            .build());
    }
}

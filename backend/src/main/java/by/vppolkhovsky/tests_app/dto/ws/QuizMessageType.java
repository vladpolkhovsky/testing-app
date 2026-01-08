package by.vppolkhovsky.tests_app.dto.ws;

public enum QuizMessageType {
    START_GAME,
    START_ROUND,
    SHOW_QUESTION,
    SHOW_QUESTION_ANSWER,
    NOTIFY_ANSWER_SAVED,
    STOP_ROUND,
    SHOW_RATING,
    SHOW_UPDATED_RATING,
    STOP_GAME,
    USER_CONNECTED,
    DISCONNECT
}

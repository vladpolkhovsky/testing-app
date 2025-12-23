package by.vppolkhovsky.tests_app;

import by.vppolkhovsky.tests_app.dto.AnswerDto;
import by.vppolkhovsky.tests_app.dto.QuestionDto;
import by.vppolkhovsky.tests_app.dto.QuizDto;

import java.util.List;
import java.util.UUID;

public class Creator {

    public static QuizDto createQuiz() {
        return QuizDto.builder()
            .id(UUID.randomUUID())
            .title("Название квиза!")
            .questions(List.of(
                QuestionDto.builder()
                    .id(UUID.randomUUID())
                    .text("Первый вопрос")
                    .textAlternative("Первый вопрос [Объяснение ответа]")
                    .price(100)
                    .answers(List.of(
                        AnswerDto.builder()
                            .id(UUID.randomUUID())
                            .isCorrect(true)
                            .optionVariant("A")
                            .optionText("Ответ на первый вопрос A")
                            .build(),
                        AnswerDto.builder()
                            .id(UUID.randomUUID())
                            .isCorrect(false)
                            .optionVariant("Б")
                            .optionText("Ответ на первый вопрос Б")
                            .build()
                    ))
                    .build(),
                QuestionDto.builder()
                    .id(UUID.randomUUID())
                    .text("Второй вопрос")
                    .price(100)
                    .answers(List.of(
                        AnswerDto.builder()
                            .id(UUID.randomUUID())
                            .isCorrect(true)
                            .optionVariant("A")
                            .optionText("Второй вопрос A")
                            .build(),
                        AnswerDto.builder()
                            .id(UUID.randomUUID())
                            .isCorrect(false)
                            .optionVariant("Б")
                            .optionText("Второй вопрос Б")
                            .build()
                    ))
                    .build()
            ))
            .build();
    }
}

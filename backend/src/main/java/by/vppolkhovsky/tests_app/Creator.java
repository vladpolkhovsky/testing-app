package by.vppolkhovsky.tests_app;

import by.vppolkhovsky.tests_app.dto.AnswerDto;
import by.vppolkhovsky.tests_app.dto.QuestionDto;
import by.vppolkhovsky.tests_app.dto.QuizDto;

import java.util.List;
import java.util.UUID;

public class Creator {

    public static QuizDto createQuiz() {
        return QuizDto.builder()
            .id(UUID.randomUUID().toString())
            .questions(List.of(
                QuestionDto.builder()
                    .id(UUID.randomUUID().toString())
                    .text("Первый вопрос")
                    .imageUrl("https://i.guim.co.uk/img/media/327aa3f0c3b8e40ab03b4ae80319064e401c6fbc/377_133_3542_2834/master/3542.jpg?width=1900&dpr=2&s=none&crop=none")
                    .replaceText("Первый вопрос [изменено]")
                    .replaceImageUrl("https://doctor-veterinar.ru/media/k2/items/cache/675d28c04794e3c683f4419536c4c15f_XL.jpg")
                    .price(100)
                    .answers(List.of(
                        AnswerDto.builder()
                            .id(UUID.randomUUID().toString())
                            .isCorrect(true)
                            .optionVariant("A")
                            .optionText("Первый вопрос A")
                            .build(),
                        AnswerDto.builder()
                            .id(UUID.randomUUID().toString())
                            .isCorrect(false)
                            .optionVariant("Б")
                            .optionText("Первый вопрос Б")
                            .build()
                    ))
                    .build(),
                QuestionDto.builder()
                    .id(UUID.randomUUID().toString())
                    .text("Второй вопрос")
                    .price(100)
                    .answers(List.of(
                        AnswerDto.builder()
                            .id(UUID.randomUUID().toString())
                            .isCorrect(true)
                            .optionVariant("A")
                            .optionText("Второй вопрос A")
                            .build(),
                        AnswerDto.builder()
                            .id(UUID.randomUUID().toString())
                            .isCorrect(false)
                            .optionVariant("Б")
                            .optionText("Второй вопрос Б")
                            .build()
                    ))
                    .build(),
                QuestionDto.builder()
                    .id(UUID.randomUUID().toString())
                    .text("Третий вопрос")
                    .price(100)
                    .answers(List.of(
                        AnswerDto.builder()
                            .id(UUID.randomUUID().toString())
                            .isCorrect(true)
                            .optionVariant("A")
                            .optionText("Третий вопрос A")
                            .build(),
                        AnswerDto.builder()
                            .id(UUID.randomUUID().toString())
                            .isCorrect(false)
                            .optionVariant("Б")
                            .optionText("Третий вопрос Б")
                            .build()
                    ))
                    .build()
            ))
            .build();
    }
}

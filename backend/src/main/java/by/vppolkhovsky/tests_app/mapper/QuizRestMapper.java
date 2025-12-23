package by.vppolkhovsky.tests_app.mapper;

import by.vppolkhovsky.tests_app.dto.AnswerDto;
import by.vppolkhovsky.tests_app.dto.QuestionDto;
import by.vppolkhovsky.tests_app.dto.QuizDto;
import by.vppolkhovsky.tests_app.dto.jpa.QuizV1;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuizRestMapper {

    QuizDto toDto(QuizV1 quiz);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "price", constant = "1000")
    QuestionDto toDto(QuizV1.QuestionV1 question);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    AnswerDto toDto(QuizV1.AnswerV1 answer);

    QuizV1 mapToV1(QuizDto quiz);
}

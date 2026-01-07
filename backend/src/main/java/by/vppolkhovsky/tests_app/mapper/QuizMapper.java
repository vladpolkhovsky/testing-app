package by.vppolkhovsky.tests_app.mapper;

import by.vppolkhovsky.tests_app.dto.AnswerDto;
import by.vppolkhovsky.tests_app.dto.QuestionDto;
import by.vppolkhovsky.tests_app.dto.QuizDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuizMapper {

    @Mapping(target = "questions", source = "questions", qualifiedByName = "toShuffledQuestion")
    QuizDto toShuffled(QuizDto quizDto);

    @Named("toShuffledQuestion")
    List<QuestionDto> toShuffledQuestion(List<QuestionDto> questions);

    QuestionDto copy(QuestionDto questionDto);

    default List<AnswerDto> toShuffledAnswer(List<AnswerDto> answerDtos) {
        List<AnswerDto> shuffled = new ArrayList<>(answerDtos.stream().map(this::copy).toList());
        Collections.shuffle(shuffled);
        for (int i = 0; i < shuffled.size(); i++) {
            AnswerDto answer = shuffled.get(i);
            answer.setOptionVariant(String.valueOf((char) ('A' + i)));
        }
        return shuffled;
    }

    AnswerDto copy(AnswerDto answerDto);
}

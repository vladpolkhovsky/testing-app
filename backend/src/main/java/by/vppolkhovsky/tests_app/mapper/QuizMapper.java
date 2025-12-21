package by.vppolkhovsky.tests_app.mapper;

import by.vppolkhovsky.tests_app.dto.AnswerDto;
import by.vppolkhovsky.tests_app.dto.QuestionDto;
import by.vppolkhovsky.tests_app.dto.QuizContext;
import by.vppolkhovsky.tests_app.dto.UserRatingDto;
import by.vppolkhovsky.tests_app.dto.ws.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuizMapper {

    @Mapping(target = "ratingItems", source = "userRating", qualifiedByName = "resolveRatingItems")
    @Mapping(target = "type", constant = "INIT_MESSAGE")
    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "question", source = "quizContext", qualifiedByName = "questionResolver")
    @Mapping(target = "nextQuestionId", source = "currentQuestionId")
    @Mapping(target = "answers", source = "quizContext", qualifiedByName = "answerResolver")
    @Mapping(target = "questionId", source = "currentQuestionId")
    WsQuizInitMessageDto toInitMessage(QuizContext quizContext);

    @Mapping(target = "type", constant = "NEW_QUESTION")
    @Mapping(target = "question", source = "quizContext", qualifiedByName = "questionResolver")
    @Mapping(target = "nextQuestionId", source = "currentQuestionId")
    @Mapping(target = "answers", source = "quizContext", qualifiedByName = "answerResolver")
    @Mapping(target = "questionId", source = "currentQuestionId")
    WsQuizShowNewQuestionMessageDto toNewQuestionMessage(QuizContext quizContext);

    @Mapping(target = "type", constant = "NEW_RATING")
    @Mapping(target = "ratingItems", source = "userRating", qualifiedByName = "resolveRatingItems")
    WsQuizNewRatingMessageDto toNewRating(QuizContext quizContext);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userId", source = "user.userId")
    WsRatingItemDto toRatingItem(UserRatingDto userRatingDto);

    WsQuestionDto toQuestion(QuestionDto question);

    List<WsAnswerOptionDto> toAnswers(List<AnswerDto> answer);

    @Mapping(target = "correct", source = "isCorrect")
    WsAnswerOptionDto toAnswer(AnswerDto answer);

    @Named("questionResolver")
    default WsQuestionDto questionResolver(QuizContext quizContext) {
        return quizContext.getQuiz().getQuestions().stream()
            .filter(q -> Objects.equals(q.getId(), quizContext.getCurrentQuestionId()))
            .findAny()
            .map(this::toQuestion)
            .orElse(null);
    }

    @Named("answerResolver")
    default List<WsAnswerOptionDto> answerResolver(QuizContext quizContext) {
        return quizContext.getQuiz().getQuestions().stream()
            .filter(q -> Objects.equals(q.getId(), quizContext.getCurrentQuestionId()))
            .findAny()
            .map(QuestionDto::getAnswers)
            .map(this::toAnswers)
            .orElse(null);
    }

    @Named("resolveRatingItems")
    default List<WsRatingItemDto> resolveRatingItems(Map<String, UserRatingDto> map) {
        return map.values().stream()
            .map(this::toRatingItem)
            .toList();
    }
}

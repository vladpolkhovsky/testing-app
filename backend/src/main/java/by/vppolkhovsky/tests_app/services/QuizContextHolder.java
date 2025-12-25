package by.vppolkhovsky.tests_app.services;

import by.vppolkhovsky.tests_app.Creator;
import by.vppolkhovsky.tests_app.dto.QuizContext;
import by.vppolkhovsky.tests_app.dto.QuizDto;
import by.vppolkhovsky.tests_app.mapper.QuizMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QuizContextHolder {

    private final Map<String, QuizContext> quizContexts = new HashMap<>();
    private final Map<String, LocalDateTime> quizContextsLastUpdate = new HashMap<>();
    private final QuizMapper quizMapper;

    public QuizContextHolder(QuizMapper quizMapper) {
        this.quizMapper = quizMapper;
    }

    public QuizContext createContext(QuizDto quizDto) {
        return createContext(UUID.randomUUID().toString(), quizDto);
    }

    public QuizContext createContext(String contextId, QuizDto quizDto) {
        quizContexts.put(contextId, QuizContext.builder()
            .id(contextId)
            .currentRound(1)
            .maxRounds(quizDto.getQuestions().size())
            .quiz(quizMapper.toShuffled(quizDto))
            .build());

        quizContextsLastUpdate.put(contextId, LocalDateTime.now());

        return quizContexts.get(contextId);
    }

    public QuizContext getContext(String contextId) {
        if (quizContexts.get(contextId) == null) {
            return createContext(contextId, Creator.createQuiz());
        }
        quizContextsLastUpdate.put(contextId, LocalDateTime.now());
        return quizContexts.get(contextId);
    }
}

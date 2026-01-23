package by.vppolkhovsky.tests_app.services;

import by.vppolkhovsky.tests_app.Creator;
import by.vppolkhovsky.tests_app.dto.QuizContext;
import by.vppolkhovsky.tests_app.dto.QuizDto;
import by.vppolkhovsky.tests_app.mapper.QuizMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class QuizContextHolder {

    private static RandomStringUtils RANDOM = RandomStringUtils.insecure();
    private final Map<String, QuizContext> quizContexts = new HashMap<>();
    private final Map<String, LocalDateTime> quizContextsLastUpdate = new HashMap<>();
    private final QuizMapper quizMapper;

    public QuizContextHolder(QuizMapper quizMapper) {
        this.quizMapper = quizMapper;
        final Integer a = 12;
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

    public String createContextId() {
        while (true) {
            String next = RANDOM.next(4, true, true);
            if (!quizContexts.containsKey(next)) {
                return next;
            }
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void deleteOldContexts() {
        LocalDateTime old = LocalDateTime.now().minusHours(2);

        List<String> oldContextIds = quizContextsLastUpdate.entrySet().stream()
            .filter(entry -> entry.getValue().isBefore(old))
            .map(Map.Entry::getKey)
            .toList();

        oldContextIds.forEach(contextId -> {
            quizContexts.remove(contextId);
            quizContextsLastUpdate.remove(contextId);
        });
    }
}

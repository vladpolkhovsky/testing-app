package by.vppolkhovsky.tests_app.services;

import by.vppolkhovsky.tests_app.Creator;
import by.vppolkhovsky.tests_app.dto.QuizDto;
import by.vppolkhovsky.tests_app.dto.QuizRegistryDto;
import by.vppolkhovsky.tests_app.dto.jpa.QuizV1;
import by.vppolkhovsky.tests_app.dto.jpa.QuizVersion;
import by.vppolkhovsky.tests_app.jpa.entity.QuizEntity;
import by.vppolkhovsky.tests_app.jpa.repository.QuizRepository;
import by.vppolkhovsky.tests_app.mapper.QuizRestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizJpaService {

    private final QuizRepository quizRepository;
    private final QuizRestMapper quizRestMapper;

    public QuizJpaService(QuizRepository quizRepository, QuizRestMapper quizRestMapper) {
        this.quizRepository = quizRepository;
        this.quizRestMapper = quizRestMapper;
    }

    @Transactional(readOnly = true)
    public Optional<QuizDto> get(UUID id) {
        return quizRepository.findById(id)
            .map(QuizEntity::getContent)
            .map(quizRestMapper::toDto)
            .or(() -> Optional.of(Creator.createQuiz()))
            .map(quiz -> {
                quiz.setId(id);
                return quiz;
            });
    }

    @Transactional
    public QuizDto save(UUID id, QuizDto quiz) {
        Optional<QuizEntity> foundById = quizRepository.findById(id);

        QuizEntity entity;
        if (foundById.isPresent()) {
            entity = foundById.get();
        } else {
            entity = new QuizEntity();
            entity.setId(id);
        }

        QuizV1 quizV1 = quizRestMapper.mapToV1(quiz);
        quizV1.setId(id);

        quizV1.getQuestions().forEach(question -> {
            for (int i = 0; i < question.getAnswers().size(); i++) {
                QuizV1.AnswerV1 answer = question.getAnswers().get(i);
                answer.setOptionVariant(String.valueOf((char) ('A' + i)));
            }
        });

        entity.setTitle(quizV1.getTitle());
        entity.setQuizVersion(QuizVersion.V1);
        entity.setContent(quizV1);

        entity = quizRepository.save(entity);

        return quizRestMapper.toDto(entity.getContent());
    }

    @Transactional(readOnly = true)
    public List<QuizRegistryDto> list() {
        return quizRepository.findTop100ByOrderByUpdatedAtDesc().map(quiz -> QuizRegistryDto.builder()
                .title(quiz.getTitle())
                .id(quiz.getId())
                .build())
            .toList();
    }

    @Transactional
    public void delete(UUID id) {
        quizRepository.deleteById(id);
    }
}

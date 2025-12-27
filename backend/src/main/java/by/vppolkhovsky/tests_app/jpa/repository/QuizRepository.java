package by.vppolkhovsky.tests_app.jpa.repository;

import by.vppolkhovsky.tests_app.jpa.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
    @Query("from QuizEntity where createdBy.id = :userId order by createdAt desc")
    Stream<QuizEntity> findUserQuizzes(UUID userId);
}

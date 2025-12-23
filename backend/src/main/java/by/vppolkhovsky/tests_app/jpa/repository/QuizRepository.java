package by.vppolkhovsky.tests_app.jpa.repository;

import by.vppolkhovsky.tests_app.jpa.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
    Stream<QuizEntity> findTop100ByOrderByUpdatedAtDesc();
}

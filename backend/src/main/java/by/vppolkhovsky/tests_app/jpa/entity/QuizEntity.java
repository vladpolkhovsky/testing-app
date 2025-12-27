package by.vppolkhovsky.tests_app.jpa.entity;

import by.vppolkhovsky.tests_app.dto.jpa.QuizV1;
import by.vppolkhovsky.tests_app.dto.jpa.QuizVersion;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "quizzes")
public class QuizEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false, columnDefinition = "jsonb")
    private QuizV1 content;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_version", nullable = false, length = 50)
    private QuizVersion quizVersion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime updatedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
}

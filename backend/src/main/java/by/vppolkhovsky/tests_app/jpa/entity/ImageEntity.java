package by.vppolkhovsky.tests_app.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "images")
public class ImageEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 512)
    private String name;

    @Lob
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "content", columnDefinition = "BYTEA")
    private byte[] content;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "timestamp")
    private LocalDateTime updatedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "timestamp")
    private LocalDateTime createdAt;
}

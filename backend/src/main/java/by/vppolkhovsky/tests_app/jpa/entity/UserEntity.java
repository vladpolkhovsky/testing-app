package by.vppolkhovsky.tests_app.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50, columnDefinition = "varchar(50)")
    private String username;

    @Column(name = "password", nullable = false, length = 512, columnDefinition = "varchar(512)")
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime updatedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime createdAt;
}

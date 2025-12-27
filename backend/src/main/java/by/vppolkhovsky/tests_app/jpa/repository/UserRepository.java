package by.vppolkhovsky.tests_app.jpa.repository;

import by.vppolkhovsky.tests_app.jpa.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

    Optional<UserEntity> findFirstByUsername(String username);
}

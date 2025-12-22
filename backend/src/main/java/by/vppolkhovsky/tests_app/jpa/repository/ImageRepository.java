package by.vppolkhovsky.tests_app.jpa.repository;

import by.vppolkhovsky.tests_app.jpa.entity.ImageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends CrudRepository<ImageEntity, UUID> {

}

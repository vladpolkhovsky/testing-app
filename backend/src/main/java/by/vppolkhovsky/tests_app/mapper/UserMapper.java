package by.vppolkhovsky.tests_app.mapper;

import by.vppolkhovsky.tests_app.dto.UserDto;
import by.vppolkhovsky.tests_app.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserDto toDto(UserEntity entity);
}

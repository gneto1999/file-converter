package dev.gneto.file_converter.mapper;

import dev.gneto.file_converter.dto.response.UserResponse;
import dev.gneto.file_converter.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "publicId", target = "id")
    UserResponse userToUserResponse(User user);
}

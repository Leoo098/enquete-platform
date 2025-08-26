package com.project.enquete.core.enquete_platform.controller.mappers;

import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO dto);

    UserDTO toDTO(User user);
}

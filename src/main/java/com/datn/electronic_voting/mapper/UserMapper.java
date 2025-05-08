package com.datn.electronic_voting.mapper;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);
}

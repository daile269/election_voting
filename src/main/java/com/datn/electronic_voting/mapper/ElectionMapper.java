package com.datn.electronic_voting.mapper;

import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.entity.Election;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ElectionMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ElectionDTO toDTO(Election election);

    Election toEntity(ElectionDTO electionDTO);
}

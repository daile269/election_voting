package com.datn.electronic_voting.mapper;

import com.datn.electronic_voting.dto.CandidateDTO;
import com.datn.electronic_voting.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CandidateMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "gender", source = "gender")
    CandidateDTO toDTO(Candidate candidate);

    @Mapping(target = "gender", source = "gender")
    Candidate toEntity(CandidateDTO candidateDTO);
}

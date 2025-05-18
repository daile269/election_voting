package com.datn.electronic_voting.mapper;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.entity.ElectionCandidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ElectionCandidateMapper {

    @Mapping(target = "electionId", source = "election.id")
    @Mapping(target = "candidateId", source = "candidate.id")
    ElectionCandidateDTO toDTO(ElectionCandidate electionCandidate);

    @Mapping(target = "election.id", source = "electionId")
    @Mapping(target = "candidate.id", source = "candidateId")
    ElectionCandidate toEntity(ElectionCandidateDTO electionCandidateDTO);
}

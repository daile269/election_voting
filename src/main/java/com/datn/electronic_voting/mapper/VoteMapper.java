package com.datn.electronic_voting.mapper;

import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    VoteDTO toDTO(Vote vote);

    Vote toEntity(VoteDTO voteDTO);
}

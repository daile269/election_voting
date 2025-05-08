package com.datn.electronic_voting.mapper;

import com.datn.electronic_voting.dto.ResultDTO;
import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.entity.Result;
import com.datn.electronic_voting.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ResultMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ResultDTO toDTO(Result result);

    Result toEntity(ResultDTO resultDTO);
}

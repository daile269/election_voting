package com.datn.electronic_voting.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddCandidatesRequest {
    private List<Long> candidateIds;
}

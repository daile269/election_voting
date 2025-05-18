package com.datn.electronic_voting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionCandidateDTO {
    private Long electionId;
    private Long candidateId;
    private int voteCount;
}

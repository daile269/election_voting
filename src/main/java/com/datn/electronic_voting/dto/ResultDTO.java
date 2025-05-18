package com.datn.electronic_voting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDTO {
    private Long electionId;
    private List<TallyDTO> tallies;

    private int totalVotes;

    public ResultDTO(Long electionId, List<TallyDTO> tallies) {
        this.electionId = electionId;
        this.tallies = tallies;
        this.totalVotes = tallies.stream().mapToInt(TallyDTO::getVotes).sum();
    }
}

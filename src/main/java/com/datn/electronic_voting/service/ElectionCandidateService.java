package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.ResultDTO;

import java.util.List;

public interface ElectionCandidateService {
    List<ResultDTO> getAllResults();
    ResultDTO getResultForElection(Long electionId);

    void countVoteForElection(Long electionId,Long candidateId);
}

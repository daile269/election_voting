package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.VoteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoteService {
    VoteDTO createVote(VoteDTO voteDTO, boolean voteChoice);

    VoteDTO updateVote(VoteDTO voteDTO, Long id,boolean voteChoice);

    List<VoteDTO> getAllVotes();
    List<VoteDTO> getVotesPageable(Pageable pageable);

    List<VoteDTO> getVoteByUserId(Long userId,Pageable pageable);

    VoteDTO findVoteById(Long id);

    void deleteVote(Long id);

    int countAgreeVotes(Long electionId,Long candidateId);

    List<VoteDTO> getVoteByElectionAndCandidateId(Long electionId, Long candidateId);
    List<VoteDTO> getVotesAndFilter(Long electionId);
    public Page<VoteDTO> getVotesAndFilter(Long electionId, int page, int size);

    int countVoteCandidateInElection(Long electionId,Long candidateId);
    int totalItem();

    int totalItemVotesForUser(Long userId);
}

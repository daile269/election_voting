package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.entity.Vote;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

public interface VoteService {
    VoteDTO createVote(VoteDTO voteDTO, boolean voteChoice);

    VoteDTO updateVote(VoteDTO voteDTO, Long id,boolean voteChoice);

    List<VoteDTO> getAllVotes();
    List<VoteDTO> getVotesPageable(Pageable pageable);

    VoteDTO findVoteById(Long id);

    void deleteVote(Long id);

    int countAgreeVotes(Long electionId,Long candidateId);

    List<VoteDTO> getVoteByElectionAndCandidateId(Long electionId, Long candidateId);
    List<VoteDTO> getVotesByElectionId(Long electionId);

    int countVoteCandidateInElection(Long electionId,Long candidateId);
    int totalItem();
}

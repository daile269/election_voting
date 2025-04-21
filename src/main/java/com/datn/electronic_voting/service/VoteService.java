package com.datn.electronic_voting.service;

import com.datn.electronic_voting.entity.Vote;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

public interface VoteService {
    Vote createVote(Vote vote,boolean voteChoice);

    Vote updateVote(Vote vote, Long id,boolean voteChoice);

    List<Vote> getAllVotes();
    List<Vote> getVotesPageable(Pageable pageable);

    Vote findVoteById(Long id);

    void deleteVote(Long id);

    int countAgreeVotes(Long electionId,Long candidateId);

    List<Vote> getVoteByElectionAndCandidateId(Long electionId, Long candidateId);

    int countVoteCandidateInElection(Long electionId,Long candidateId);
}

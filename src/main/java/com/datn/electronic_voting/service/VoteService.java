package com.datn.electronic_voting.service;

import com.datn.electronic_voting.entity.Vote;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoteService {
    Vote createVote(Vote vote);

    Vote updateVote(Vote vote, Long id);

    List<Vote> getAllVotes();
    List<Vote> getVotesPageable(Pageable pageable);

    Vote findVoteById(Long id);

    void deleteVote(Long id);
}

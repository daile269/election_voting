package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.entity.Vote;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.repositories.UserRepository;
import com.datn.electronic_voting.repositories.VoteRepository;
import com.datn.electronic_voting.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IVoteService implements VoteService {

    private final VoteRepository voteRepository;

    private final ElectionRepository electionRepository;

    private final UserRepository userRepository;

    private final CandidateRepository candidateRepository;

    @Override
    public Vote createVote(Vote vote) {
        checkInforVote(vote);
        return voteRepository.save(vote);
    }

    @Override
    public Vote updateVote(Vote vote,Long id) {
        vote.setId(id);
        Vote rs = voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        vote.setVoteTime(rs.getVoteTime());
        checkInforVote(vote);
        return voteRepository.save(vote);
    }

    @Override
    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    @Override
    public List<Vote> getVotesPageable(Pageable pageable) {
        return voteRepository.findAll(pageable).getContent();
    }

    @Override
    public Vote findVoteById(Long id) {
        return voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
    }

    @Override
    public void deleteVote(Long id) {
        voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        voteRepository.deleteById(id);
    }
    private void checkInforVote(Vote vote){
        electionRepository.findById(vote.getElectionId()).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        userRepository.findById(vote.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        candidateRepository.findById(vote.getCandidateId()).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
    }
}

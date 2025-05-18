package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.dto.ResultDTO;
import com.datn.electronic_voting.dto.TallyDTO;
import com.datn.electronic_voting.entity.ElectionCandidate;
import com.datn.electronic_voting.mapper.ElectionCandidateMapper;
import com.datn.electronic_voting.repositories.ElectionCandidateRepository;
import com.datn.electronic_voting.repositories.VoteRepository;
import com.datn.electronic_voting.service.ElectionCandidateService;
import com.datn.electronic_voting.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionCandidateImpl implements ElectionCandidateService {

    private final ElectionCandidateRepository electionCandidateRepository;

    private final VoteRepository voteRepository;

    private final VoteService voteService;

    @Override
    public List<ResultDTO> getAllResults() {
        List<ElectionCandidate> results = electionCandidateRepository.findAll();
        Map<Long,List<ElectionCandidate>> grouped = results.stream()
                .collect(Collectors.groupingBy(ec->ec.getElection().getId()));
        return grouped.entrySet().stream()
                .map(entry->{
                    Long electionId = entry.getKey();
                    List<TallyDTO> tallies = entry.getValue().stream()
                            .map(ec -> {
                                int agree = voteService.countAgreeVotes(electionId, ec.getCandidate().getId());
                                int total = ec.getVoteCount();
                                int disagree = total - agree;
                                return new TallyDTO(ec.getCandidate().getId(), ec.getVoteCount(), agree, disagree);
                            })
                            .collect(Collectors.toList());

                    return new ResultDTO(electionId,tallies);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ResultDTO getResultForElection(Long electionId) {
        List<ElectionCandidate> results = electionCandidateRepository.findByElectionId(electionId);
        List<TallyDTO> tallies = results.stream()
                .map(ec-> {
                    int agree = voteService.countAgreeVotes(electionId, ec.getCandidate().getId());
                    int total = ec.getVoteCount();
                    int disagree = total - agree;
                    return new TallyDTO(ec.getCandidate().getId(), ec.getVoteCount(), agree, disagree);
                })
                .collect(Collectors.toList());
        return new ResultDTO(electionId,tallies);
    }

    @Override
    public void countVoteForElection(Long electionId, Long candidateId) {
        int voteCount = voteRepository.countVoteCandidateInElection(electionId,candidateId);
        ElectionCandidate result =  electionCandidateRepository.findByElectionIdAndCandidateId(electionId,candidateId);
        result.setVoteCount(voteCount);
        electionCandidateRepository.save(result);
    }


}

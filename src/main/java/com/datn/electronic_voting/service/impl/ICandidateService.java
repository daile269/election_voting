package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ICandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final ElectionRepository electionRepository;
    @Override
    public Candidate createCandidate(Candidate candidate) {
        electionRepository.findById(candidate.getElectionId()).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        return candidateRepository.save(candidate);
    }

    @Override
    public Candidate updateCandidate(Candidate candidate,Long id) {
        candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        candidate.setId(id);
        electionRepository.findById(candidate.getElectionId()).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        return candidateRepository.save(candidate);
    }

    @Override
    public List<Candidate> getAllCandidate() {
        return candidateRepository.findAll();
    }

    @Override
    public List<Candidate> getCandidatePageable(Pageable pageable) {
        return candidateRepository.findAll(pageable).getContent();
    }

    @Override
    public Candidate findCandidateById(Long id) {
        return candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
    }

    @Override
    public void deleteCandidate(Long id) {
        candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        candidateRepository.deleteById(id);
    }
}

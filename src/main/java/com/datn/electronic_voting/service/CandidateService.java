package com.datn.electronic_voting.service;

import com.datn.electronic_voting.entity.Candidate;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CandidateService {
    Candidate createCandidate(Candidate candidate);

    Candidate updateCandidate(Candidate candidate, Long id);

    List<Candidate> getAllCandidate();

    List<Candidate> getCandidatePageable(Pageable pageable);
    Candidate findCandidateById(Long id);

    void deleteCandidate(Long id);
}

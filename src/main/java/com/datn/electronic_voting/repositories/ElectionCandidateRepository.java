package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.ElectionCandidate;
import com.datn.electronic_voting.entity.ElectionCandidateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionCandidateRepository extends JpaRepository<ElectionCandidate, ElectionCandidateId> {
    List<ElectionCandidate> findByElectionId(Long electionId);

    ElectionCandidate findByElectionIdAndCandidateId(Long electionId,Long candidateId);

    int countByElectionId(Long electionId);
}

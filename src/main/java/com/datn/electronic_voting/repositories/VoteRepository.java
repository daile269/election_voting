package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    List<Vote> findAllByElectionIdAndCandidateId(Long electionId, Long candidateId);
}

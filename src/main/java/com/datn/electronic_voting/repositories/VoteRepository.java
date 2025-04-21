package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    List<Vote> findAllByElectionIdAndCandidateId(Long electionId, Long candidateId);

    @Query(value = "SELECT COUNT(*) FROM electronic_voting.vote WHERE election_id = :electionId AND candidate_id = :candidateId"
            , nativeQuery = true)
    int countVoteCandidateInElection(@Param("electionId") Long electionId, @Param("candidateId") Long candidateId);

}

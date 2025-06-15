package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    boolean existsByUserIdAndElectionIdAndCandidateId(Long userId, Long electionId,Long candidateId);

    List<Vote> getVotesByElectionId(Long electionId);

    List<Vote> getVoteByUserId(Long userId, Pageable pageable);

    List<Vote> getAllVoteByUserId(Long userId);

    @Query("""
    SELECT v FROM Vote v
    WHERE (:electionId IS NULL OR v.electionId = :electionId)
""")
    Page<Vote> findVotesByElectionIdOptional(@Param("electionId") Long electionId, Pageable pageable);


}

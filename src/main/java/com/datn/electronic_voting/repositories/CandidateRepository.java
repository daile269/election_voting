package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate,Long> {
    boolean existsByEmail(String email);

    @Query(value = "SELECT DISTINCT c.* FROM vote v JOIN candidate c ON v.candidate_id = c.id WHERE v.user_id = :userId",nativeQuery = true)
    List<Candidate> findCandidatesVotedByUser(@Param("userId") Long userId);
}

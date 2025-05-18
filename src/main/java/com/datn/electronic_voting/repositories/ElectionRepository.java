package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.entity.Election;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election,Long> {
    Election findElectionByElectionCode(String electionCode);

    @Query(value = "SELECT * FROM election e " +
            "JOIN election_candidate ec ON e.id = ec.election_id " +
            "WHERE ec.candidate_id = :candidateId", nativeQuery = true)
    List<Election> findElectionsByCandidateId(@Param("candidateId") Long candidateId);

    @Query(value = "SELECT DISTINCT e.* " +
            " FROM vote v" +
            " JOIN election e ON v.election_id = e.id" +
            " WHERE v.user_id =:userId",nativeQuery = true)
    List<Election> findElectionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT DISTINCT e.* " +
            " FROM vote v" +
            " JOIN election e ON v.election_id = e.id" +
            " WHERE v.user_id =:userId",nativeQuery = true)
    List<Election> findAllElectionsByUserId(@Param("userId") Long userId);
}

package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<Election,Long> {
    Election findElectionByElectionCode(String electionCode);
}

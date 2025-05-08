package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result,Long> {
    List<Result> getResultByElectionId(Long electionId);
}

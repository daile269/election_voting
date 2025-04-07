package com.datn.electronic_voting.service;

import com.datn.electronic_voting.entity.Election;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ElectionService {

    Election createElection(Election election);

    Election updateElection(Election election, Long id);

    List<Election> getAllElections();

    List<Election> getElectionPageable(Pageable pageable);

    Election findElectionById(Long id);

    void deleteElection(Long id);

}

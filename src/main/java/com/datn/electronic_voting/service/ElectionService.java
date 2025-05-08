package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.entity.Election;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ElectionService {

    ElectionDTO createElection(ElectionDTO electionDTO);

    ElectionDTO updateElection(ElectionDTO electionDTO, Long id);

    List<ElectionDTO> getAllElections();

    List<ElectionDTO> getElectionPageable(Pageable pageable);

    ElectionDTO findElectionById(Long id);

    ElectionDTO findElectionByElectionCode(String electionCode);

    void deleteElection(Long id);
    int totalItem();

}

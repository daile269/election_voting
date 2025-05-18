package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.dto.ElectionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ElectionService {

    ElectionDTO createElection(ElectionDTO electionDTO);

    ElectionDTO updateElection(ElectionDTO electionDTO, Long id);

    void addCandidateElection(Long electionId,List<Long> candidateIds);
    void deleteCandidateElection(Long electionId,Long candidateId);
    List<ElectionDTO> getAllElections();

    List<ElectionDTO> getElectionPageable(Pageable pageable);

    List<ElectionDTO> getElectionByCandidateId(Long candidateId);

    List<ElectionDTO> getElectionByUserId(Long userId,Pageable pageable);

    ElectionDTO findElectionById(Long id);

    ElectionDTO findElectionByElectionCode(String electionCode);

    List<ElectionCandidateDTO> getCandidatesByElectionId(Long electionId);

    void addUsersToElection(Long electionId,List<Long> userIds);

    void deleteUsersToElection(Long electionId,List<Long> userIds);
    void updateElectionStatus();
    void deleteElection(Long id);
    int totalItem();

    int totalItemElectionsForUser(Long userId);
}

package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.CandidateDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CandidateService {
    CandidateDTO createCandidate(CandidateDTO candidateDTO);

    CandidateDTO updateCandidate(CandidateDTO candidateDTO, Long id);

    List<CandidateDTO> getAllCandidate();

    List<CandidateDTO> getCandidatePageable(Pageable pageable);
    CandidateDTO findCandidateById(Long id);

    void deleteCandidate(Long id);

    CandidateDTO updateImage(Long userId, MultipartFile image) throws IOException;

    List<CandidateDTO> getCandidateByElectionId(Long electionId);
    int totalItem();
}

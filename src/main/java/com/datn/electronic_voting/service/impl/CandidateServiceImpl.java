package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.CandidateDTO;
import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.CandidateMapper;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.service.CandidateService;
import com.datn.electronic_voting.untils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final ElectionRepository electionRepository;

    private final S3Service s3Service;

    private final CandidateMapper candidateMapper;
    @Override
    public CandidateDTO createCandidate(CandidateDTO candidateDTO) {
        Candidate candidate = candidateMapper.toEntity(candidateDTO);
        electionRepository.findById(candidateDTO.getElectionId()).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        if (candidateRepository.existsByEmail(candidateDTO.getEmail())) throw new AppException(ErrorCode.EMAIL_IS_EXISTS);
        candidateRepository.save(candidate);
        return candidateMapper.toDTO(candidateRepository.save(candidate));
    }

    @Override
    public CandidateDTO updateCandidate(CandidateDTO candidateDTO, Long id) {
        Candidate candidate = candidateMapper.toEntity(candidateDTO);
        Candidate candidateRs = candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        electionRepository.findById(candidate.getElectionId()).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        candidate.setId(id);
        candidate.setUrlAvatar(candidateRs.getUrlAvatar());
        candidateRepository.save(candidate);
        return candidateMapper.toDTO(candidateRepository.save(candidate));
    }

    @Override
    public List<CandidateDTO> getAllCandidate() {
        return candidateRepository.findAll().stream()
                .map(candidate -> candidateMapper.toDTO(candidate)).collect(Collectors.toList());
    }

    @Override
    public List<CandidateDTO> getCandidatePageable(Pageable pageable) {
        return candidateRepository.findAll(pageable).getContent()
                .stream().map(candidate -> candidateMapper.toDTO(candidate)).collect(Collectors.toList());
    }

    @Override
    public CandidateDTO findCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        return candidateMapper.toDTO(candidate);
    }

    @Override
    public void deleteCandidate(Long id) {
        candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        candidateRepository.deleteById(id);
    }

    @Override
    public CandidateDTO updateImage(Long candidateId, MultipartFile image) throws IOException {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        String urlAvatar = s3Service.uploadFile(image);
        candidate.setUrlAvatar(urlAvatar);
        candidateRepository.save(candidate);
        return candidateMapper.toDTO(candidateRepository.save(candidate));
    }

    @Override
    public List<CandidateDTO> getCandidateByElectionId(Long electionId) {
        List<Candidate> candidateList = candidateRepository.getCandidateByElectionId(electionId);
        return candidateList.stream()
                .map(candidate -> candidateMapper.toDTO(candidate)).collect(Collectors.toList());
    }

    @Override
    public int totalItem() {
        return (int) candidateRepository.count();
    }
}

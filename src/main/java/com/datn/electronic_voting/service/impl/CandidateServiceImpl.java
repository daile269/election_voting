package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.CandidateDTO;
import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.entity.ElectionCandidate;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.CandidateMapper;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionCandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.service.CandidateService;
import com.datn.electronic_voting.untils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final ElectionRepository electionRepository;

    private final S3Service s3Service;

    private final CandidateMapper candidateMapper;

    private final ElectionCandidateRepository electionCandidateRepository;
    @Override
    public CandidateDTO createCandidate(CandidateDTO candidateDTO) {
        Candidate candidate = candidateMapper.toEntity(candidateDTO);
        if (candidateRepository.existsByEmail(candidateDTO.getEmail())) throw new AppException(ErrorCode.EMAIL_IS_EXISTS);
        return candidateMapper.toDTO(candidateRepository.save(candidate));
    }

    @Override
    public CandidateDTO updateCandidate(CandidateDTO candidateDTO, Long id) {
        Candidate candidate = candidateMapper.toEntity(candidateDTO);
        Candidate candidateRs = candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        candidate.setId(id);
        candidate.setUrlAvatar(candidateRs.getUrlAvatar());
        candidate.setElections(candidateRs.getElections());
        return candidateMapper.toDTO(candidateRepository.save(candidate));
    }

    @Override
    public CandidateDTO addElectionCandidate(Long candidateId, Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        if(candidate.getElections().contains(election)) throw new AppException(ErrorCode.ELECTION_CANDIDATE_EXIST);
//        candidate.getElections().add(election);
//        election.getCandidateList().add(candidate);
        return candidateMapper.toDTO(candidateRepository.save(candidate));
    }

    @Override
    public CandidateDTO deleteElectionCandidate(Long candidateId, Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        if(!candidate.getElections().contains(election)) throw new AppException(ErrorCode.ELECTION_CANDIDATE_NOT_EXIST);
        candidate.getElections().remove(election);
        election.getCandidateList().remove(candidate);
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
        electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        List<ElectionCandidate> electionCandidates = electionCandidateRepository.findByElectionId(electionId);
        List<Candidate> candidates = electionCandidates.stream()
                .map(electionCandidate -> electionCandidate.getCandidate()).collect(Collectors.toList());
        return candidates.stream()
                .map(candidate -> candidateMapper.toDTO(candidate)).collect(Collectors.toList());
    }

    @Override
    public List<CandidateDTO> getCandidatesNotInElection(Long electionId) {
        electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));

        List<ElectionCandidate> electionCandidates = electionCandidateRepository.findByElectionId(electionId);
        // Lấy danh sách các candidateId đã tham gia cuộc bầu cử
        Set<Long> candidateIdsInElection = electionCandidates.stream()
                .map(electionCandidate -> electionCandidate.getCandidate().getId())
                .collect(Collectors.toSet());
        // Lấy tất cả các ứng viên
        List<Candidate> allCandidates = candidateRepository.findAll();

        // Lọc ra các ứng viên không có trong election
        List<Candidate> candidatesNotInElection = allCandidates.stream()
                .filter(candidate -> !candidateIdsInElection.contains(candidate.getId()))  // Kiểm tra nếu candidate không thuộc election
                .collect(Collectors.toList());
        return candidatesNotInElection.stream()
                .map(candidate -> candidateMapper.toDTO(candidate)).collect(Collectors.toList());
    }

    @Override
    public int totalItem() {
        return (int) candidateRepository.count();
    }
}

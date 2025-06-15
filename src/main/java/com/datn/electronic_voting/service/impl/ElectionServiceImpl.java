package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.entity.*;
import com.datn.electronic_voting.enums.ElectionStatus;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.filter.ElectionSpecification;
import com.datn.electronic_voting.mapper.ElectionCandidateMapper;
import com.datn.electronic_voting.mapper.ElectionMapper;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionCandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.repositories.UserRepository;
import com.datn.electronic_voting.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;

    private final ElectionMapper electionMapper;

    private final CandidateRepository candidateRepository;

    private final ElectionCandidateRepository electionCandidateRepository;

    private final ElectionCandidateMapper electionCandidateMapper;

    private final UserRepository userRepository;
    private final VoteChoiceCache voteChoiceCache;

    @Override
    public ElectionDTO createElection(ElectionDTO electionDTO) {
        Election election = electionMapper.toEntity(electionDTO);
        if(election.getStartTime() == null) election.setStartTime(LocalDateTime.now());
        checkTime(election.getStartTime(),election.getEndTime());
        setStatusElection(election);
        return electionMapper.toDTO(electionRepository.save(election));
    }

    @Override
    public ElectionDTO updateElection(ElectionDTO electionDTO,Long id) {
        Election electionRs = electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        Election election = electionMapper.toEntity(electionDTO);
        election.setId(id);
        if(election.getStartTime().isAfter(LocalDateTime.now())&& election.getStatus()== com.datn.electronic_voting.enums.ElectionStatus.FINISHED){
            election.setStatus(com.datn.electronic_voting.enums.ElectionStatus.UPCOMING);
        }
        if (election.getEndTime().isBefore(election.getStartTime())) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }
        if(election.getEndTime().isBefore(LocalDateTime.now())&& election.getStatus()== com.datn.electronic_voting.enums.ElectionStatus.ONGOING){
            election.setStatus(com.datn.electronic_voting.enums.ElectionStatus.FINISHED);
        }
        election.setCandidateList(electionRs.getCandidateList());
        election.setElectionCode(electionRs.getElectionCode());
        return electionMapper.toDTO(electionRepository.save(election));
    }

    @Override
    public void addCandidateElection(Long electionId,List<Long> candidateIds) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        for (Long candidateId:candidateIds){
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
            ElectionCandidate electionCandidate = new ElectionCandidate();
            electionCandidate.setElection(election);
            electionCandidate.setCandidate(candidate);
            electionCandidateRepository.save(electionCandidate);
        }
    }

    @Override
    public void deleteCandidateElection(Long electionId, Long candidateId) {
         electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
         candidateRepository.findById(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        ElectionCandidateId electionCandidateId = new ElectionCandidateId(electionId,candidateId);
        ElectionCandidate electionCandidate = electionCandidateRepository.findById(electionCandidateId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_CANDIDATE_NOT_FOUND));
        electionCandidateRepository.delete(electionCandidate);
    }

    @Override
    public List<ElectionDTO> getAllElections() {
        return electionRepository.findAll().stream()
                .map(election -> electionMapper.toDTO(election)).collect(Collectors.toList());
    }

    @Override
    public List<ElectionDTO> getElectionPageable(Pageable pageable) {
        return electionRepository.findAll(pageable).getContent()
                .stream()
                .map(election -> electionMapper.toDTO(election)).collect(Collectors.toList());
    }

    @Override
    public List<ElectionDTO> getElectionByCandidateId(Long candidateId) {
        List<Election> elections = electionRepository.findElectionsByCandidateId(candidateId);
        return elections.stream()
                .map(election -> electionMapper.toDTO(election)).collect(Collectors.toList());
    }

    @Override
    public List<ElectionDTO> getElectionByUserId(Long userId, Pageable pageable) {
        Page<Election> electionsPage = electionRepository.findElectionsByUserId(userId, pageable);
        List<Election> elections = electionsPage.getContent();
        return elections.stream().map(election -> electionMapper.toDTO(election)).collect(Collectors.toList());
    }

    @Override
    public Page<ElectionDTO> searchElections(String searchTerm, String status, int page, int size) {
        ElectionStatus electionStatus = null;
        try {
            if (status != null && !status.isEmpty()) {
                electionStatus = ElectionStatus.valueOf(status.toUpperCase());
            }
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        Pageable pageable = PageRequest.of(page-1, size, Sort.by("startTime").descending());

        Specification<Election> spec = Specification
                .where(ElectionSpecification.hasTitleLike(searchTerm))
                .and(ElectionSpecification.hasStatus(electionStatus));
        Page<Election> electionList = electionRepository.findAll(spec, pageable);
        return electionList.map(election -> electionMapper.toDTO(election));
    }

    @Override
    public ElectionDTO findElectionById(Long id) {
        Election election = electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        return electionMapper.toDTO(election);
    }

    @Override
    public ElectionDTO findElectionByElectionCode(String electionCode) {
        Election election = electionRepository.findElectionByElectionCode(electionCode);
        if(election == null ) throw new AppException(ErrorCode.ELECTION_CODE_VALID);
        return electionMapper.toDTO(election);
    }

    @Override
    public List<ElectionCandidateDTO> getCandidatesByElectionId(Long electionId) {
        List<ElectionCandidate> electionCandidates = electionCandidateRepository.findByElectionId(electionId);
        return electionCandidates.stream()
                .map(electionCandidate -> electionCandidateMapper.toDTO(electionCandidate)).collect(Collectors.toList());
    }

    @Override
    public void addUsersToElection(Long electionId, List<Long> userIds) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));

        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            election.getUsers().add(user);
            user.getElections().add(election);
        }
        electionRepository.save(election);
    }

    @Override
    public void deleteUsersToElection(Long electionId, List<Long> userIds) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));

        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            election.getUsers().remove(user);
            user.getElections().remove(election);
        }
        electionRepository.save(election);
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void updateElectionStatus() {
        List<Election> elections = electionRepository.findAll();
        for(Election election:elections){
            if(election.getEndTime().isBefore(LocalDateTime.now())&& election.getStatus()== com.datn.electronic_voting.enums.ElectionStatus.ONGOING){
                List<User> users = userRepository.findUserInElection(election.getId());
                Set<Long> allUserIds = users.stream()
                        .map(User::getId)
                        .collect(Collectors.toSet());
                if (voteChoiceCache.anyCandidateNotFullyVoted(election.getId(), allUserIds)) {
                    election.setStatus(com.datn.electronic_voting.enums.ElectionStatus.CANCELLED);
                }else {
                    election.setStatus(com.datn.electronic_voting.enums.ElectionStatus.FINISHED);
                }

                electionRepository.save(election);
                voteChoiceCache.clearElection(election.getId());
            }
            if(election.getStartTime().isBefore(LocalDateTime.now()) && election.getStatus()== ElectionStatus.UPCOMING){
                election.setStatus(com.datn.electronic_voting.enums.ElectionStatus.ONGOING);
                electionRepository.save(election);
            }
        }
    }

    @Override
    public void deleteElection(Long id) {
        Election election = electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        election.getUsers().clear();
        election.getCandidateList().clear();
        electionRepository.save(election);
        electionRepository.deleteById(id);
    }

    @Override
    public int totalItem() {
        return (int) electionRepository.count();
    }

    @Override
    public int totalItemElectionsForUser(Long userId) {
        return electionRepository.countUserElectionsByUserId(userId);
    }

    public void checkTime(LocalDateTime startTime, LocalDateTime endTime){
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.START_TIME_ERROR);
        }
        if (endTime.isBefore(startTime)) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }

    }
    public void setStatusElection(Election election){
        if((LocalDateTime.now()).isBefore(election.getStartTime())){
            election.setStatus(com.datn.electronic_voting.enums.ElectionStatus.UPCOMING);
        }
    }
}

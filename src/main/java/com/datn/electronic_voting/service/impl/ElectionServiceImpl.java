package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.entity.*;
import com.datn.electronic_voting.enums.ElectronStatus;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.ElectionCandidateMapper;
import com.datn.electronic_voting.mapper.ElectionMapper;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionCandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.repositories.UserRepository;
import com.datn.electronic_voting.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        if(election.getStartTime().isAfter(LocalDateTime.now())&& election.getStatus()==ElectronStatus.FINISHED){
            election.setStatus(ElectronStatus.UPCOMING);
        }
        if (election.getEndTime().isBefore(election.getStartTime())) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }
        if(election.getEndTime().isBefore(LocalDateTime.now())&& election.getStatus()==ElectronStatus.ONGOING){
            election.setStatus(ElectronStatus.FINISHED);
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
        List<Election> elections = electionRepository.findElectionsByUserId(userId,pageable);
        return elections.stream().map(election -> electionMapper.toDTO(election)).collect(Collectors.toList());
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
            if(election.getEndTime().isBefore(LocalDateTime.now())&& election.getStatus()==ElectronStatus.ONGOING){
                election.setStatus(ElectronStatus.FINISHED);
                electionRepository.save(election);
            }
            if(election.getStartTime().isBefore(LocalDateTime.now()) && election.getStatus()==ElectronStatus.UPCOMING){
                election.setStatus(ElectronStatus.ONGOING);
                electionRepository.save(election);
            }
//            if(election.getStartTime().isAfter(LocalDateTime.now())&& election.getStatus()==ElectronStatus.FINISHED){
//                election.setStatus(ElectronStatus.UPCOMING);
//            }
        }
    }

    @Override
    public void deleteElection(Long id) {
        electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        electionRepository.deleteById(id);
    }

    @Override
    public int totalItem() {
        return (int) electionRepository.count();
    }

    @Override
    public int totalItemElectionsForUser(Long userId) {
        List<Election> elections = electionRepository.findAllElectionsByUserId(userId);
        return elections.size();
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
            election.setStatus(ElectronStatus.UPCOMING);
        }
    }
}

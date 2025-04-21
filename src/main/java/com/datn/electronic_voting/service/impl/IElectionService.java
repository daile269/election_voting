package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IElectionService implements ElectionService {

    private final ElectionRepository electionRepository;

    @Override
    public Election createElection(Election election) {
        if(election.getStartTime() == null) election.setStartTime(LocalDateTime.now());
        checkTime(election.getStartTime(),election.getEndTime());
        return electionRepository.save(election);
    }

    @Override
    public Election updateElection(Election election,Long id) {
        electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        election.setId(id);
        if (election.getEndTime().isBefore(election.getStartTime())) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }
        return electionRepository.save(election);
    }

    @Override
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    @Override
    public List<Election> getElectionPageable(Pageable pageable) {
        return electionRepository.findAll(pageable).getContent();
    }

    @Override
    public Election findElectionById(Long id) {
        return electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
    }

    @Override
    public Election findElectionByElectionCode(String electionCode) {
        Election election = electionRepository.findElectionByElectionCode(electionCode);
        if(election == null ) throw new AppException(ErrorCode.ELECTION_CODE_VALID);
        return election;
    }

    @Override
    public void deleteElection(Long id) {
        electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        electionRepository.deleteById(id);
    }
    public void checkTime(LocalDateTime startTime, LocalDateTime endTime){
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.START_TIME_ERROR);
        }
        if (endTime.isBefore(startTime)) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }
    }
}

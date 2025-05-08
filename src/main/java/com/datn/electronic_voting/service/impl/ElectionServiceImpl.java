package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.ElectionMapper;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;

    private final ElectionMapper electionMapper;

    @Override
    public ElectionDTO createElection(ElectionDTO electionDTO) {
        Election election = electionMapper.toEntity(electionDTO);
        if(election.getStartTime() == null) election.setStartTime(LocalDateTime.now());
        checkTime(election.getStartTime(),election.getEndTime());
        electionRepository.save(election);
        return electionMapper.toDTO(electionRepository.save(election));
    }

    @Override
    public ElectionDTO updateElection(ElectionDTO electionDTO,Long id) {
        Election electionRs = electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        Election election = electionMapper.toEntity(electionDTO);
        election.setId(id);
        if (election.getEndTime().isBefore(election.getStartTime())) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }
        election.setElectionCode(electionRs.getElectionCode());
        electionRepository.save(election);
        return electionMapper.toDTO(electionRepository.save(election));
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
    public void deleteElection(Long id) {
        electionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        electionRepository.deleteById(id);
    }

    @Override
    public int totalItem() {
        return (int) electionRepository.count();
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

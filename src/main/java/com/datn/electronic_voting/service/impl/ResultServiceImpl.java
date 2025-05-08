package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.ResultDTO;
import com.datn.electronic_voting.entity.Result;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.ResultMapper;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.repositories.ResultRepository;
import com.datn.electronic_voting.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;

    private final CandidateRepository candidateRepository;

    private final ElectionRepository electionRepository;

    private final ResultMapper resultMapper;

    @Override
    public ResultDTO createResult(ResultDTO resultDTO) {
        Result result = resultMapper.toEntity(resultDTO);
        checkInfor(result);
        resultRepository.save(result);
        return resultMapper.toDTO(resultRepository.save(result));
    }

    @Override
    public ResultDTO updateResult(ResultDTO resultDTO,Long id) {
        Result result = resultMapper.toEntity(resultDTO);
        result.setId(id);
        checkInfor(result);
        resultRepository.save(result);
        return resultMapper.toDTO(resultRepository.save(result));
    }

    @Override
    public List<ResultDTO> getAllResults() {
        return resultRepository.findAll()
                .stream().map(result -> resultMapper.toDTO(result)).collect(Collectors.toList());
    }

    @Override
    public List<ResultDTO> getResultsPageable(Pageable pageable) {
        return resultRepository.findAll(pageable).getContent()
                .stream().map(result -> resultMapper.toDTO(result)).collect(Collectors.toList());
    }

    @Override
    public ResultDTO findResultById(Long id) {
        Result result = resultRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));
        return resultMapper.toDTO(result);
    }

    @Override
    public List<ResultDTO> getResultsByElectionId(Long electionId) {
        List<Result> results = resultRepository.getResultByElectionId(electionId);
        return results.stream().map(result -> resultMapper.toDTO(result)).collect(Collectors.toList());
    }

    @Override
    public void deleteResult(Long id) {
        resultRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));
        resultRepository.deleteById(id);
    }
    private void checkInfor(Result result){
        candidateRepository.findById(result.getCandidateId()).orElseThrow(
                () -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        electionRepository.findById(result.getElectionId()).orElseThrow(
                () -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
    }
}

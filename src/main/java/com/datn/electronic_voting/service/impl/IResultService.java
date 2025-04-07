package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.entity.Result;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.repositories.ResultRepository;
import com.datn.electronic_voting.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IResultService implements ResultService {

    private final ResultRepository resultRepository;

    private final CandidateRepository candidateRepository;

    private final ElectionRepository electionRepository;

    @Override
    public Result createResult(Result result) {
        checkInfor(result);
        return resultRepository.save(result);
    }

    @Override
    public Result updateResult(Result result,Long id) {
        result.setId(id);
        checkInfor(result);
        return resultRepository.save(result);
    }

    @Override
    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    @Override
    public List<Result> getResultsPageable(Pageable pageable) {
        return resultRepository.findAll(pageable).getContent();
    }

    @Override
    public Result findResultById(Long id) {
        return resultRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));
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

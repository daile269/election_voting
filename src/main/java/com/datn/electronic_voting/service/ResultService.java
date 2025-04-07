package com.datn.electronic_voting.service;

import com.datn.electronic_voting.entity.Result;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResultService {
    Result createResult(Result result);

    Result updateResult(Result result, Long id);

    List<Result> getAllResults();

    List<Result> getResultsPageable(Pageable pageable);

    Result findResultById(Long id);

    void deleteResult(Long id);
}

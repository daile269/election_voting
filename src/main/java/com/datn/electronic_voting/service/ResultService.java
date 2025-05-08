package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.ResultDTO;
import com.datn.electronic_voting.entity.Result;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResultService {
    ResultDTO createResult(ResultDTO resultDTO);

    ResultDTO updateResult(ResultDTO resultDTO, Long id);

    List<ResultDTO> getAllResults();

    List<ResultDTO> getResultsPageable(Pageable pageable);

    ResultDTO findResultById(Long id);

    List<ResultDTO> getResultsByElectionId(Long electionId);
    void deleteResult(Long id);
}

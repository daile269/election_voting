package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.dto.ResultDTO;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.service.ElectionCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/results")
public class ElectionCandidateController {

    private final ElectionCandidateService electionCandidateService;
    @GetMapping
    public List<ResultDTO> getResults(){
        return electionCandidateService.getAllResults();
    }

    @GetMapping("/election/{electionId}")
    public ResultDTO getResultsByElection(@PathVariable Long electionId){
        return electionCandidateService.getResultForElection(electionId);
    }



}

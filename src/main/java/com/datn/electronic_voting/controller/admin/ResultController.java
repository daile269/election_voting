package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.CandidateDTO;
import com.datn.electronic_voting.dto.ResultDTO;
import com.datn.electronic_voting.entity.Result;
import com.datn.electronic_voting.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/results")
public class ResultController {

    private final ResultService resultService;

    @GetMapping
    public List<ResultDTO> getResults(){
        return resultService.getAllResults();
    }

    @GetMapping(value = "/paginated")
    public List<ResultDTO> getResultsPageable(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return resultService.getResultsPageable(pageable);
    }

    @GetMapping("/election/{electionId}")
    public List<ResultDTO> getResultsByElection(@PathVariable Long electionId){
        return resultService.getResultsByElectionId(electionId);
    }
    @GetMapping(value = "/{id}")
    public ResultDTO getResultById(@PathVariable Long id){
        return resultService.findResultById(id);
    }

    @PostMapping
    public ResultDTO createResult(@RequestBody ResultDTO result){
        return resultService.createResult(result);
    }

    @PutMapping(value = "/{id}")
    public ResultDTO updateResult(@RequestBody ResultDTO result, @PathVariable Long id){
        return resultService.updateResult(result,id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteResult(@PathVariable Long id){
        resultService.deleteResult(id);
        return ResponseEntity.ok("Xóa thành công");
    }

}

package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public List<Candidate> getAllCandidate(){
        return candidateService.getAllCandidate();
    }

    @GetMapping("/paginated")
    public List<Candidate> getListCandidate(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return candidateService.getCandidatePageable(pageable);
    }

    @GetMapping(value = "/{id}")
    public Candidate getCandidateById(@PathVariable Long id){
        return candidateService.findCandidateById(id);
    }

    @PostMapping
    public Candidate createCandidate(@RequestBody Candidate candidate){
        return candidateService.createCandidate(candidate);
    }

    @PutMapping(value = "/{id}")
    public Candidate updateCandidate(@RequestBody Candidate candidate, @PathVariable Long id){
        return candidateService.updateCandidate(candidate,id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id){
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }


}

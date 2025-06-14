package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.CandidateDTO;
import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.PaginatedResponse;
import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public List<CandidateDTO> getAllCandidate(){
        return candidateService.getAllCandidate();
    }

    @GetMapping("/paginated")
    public PaginatedResponse<CandidateDTO> getListCandidate(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);

        return PaginatedResponse.<CandidateDTO>builder()
                .listElements(candidateService.getCandidatePageable(pageable))
                .totalPages((int) Math.ceil( (double) (candidateService.totalItem())/size))
                .build();
    }
    @GetMapping("/election/{electionId}")
    public List<CandidateDTO> getCandidateByElection(@PathVariable Long electionId){
        return candidateService.getCandidateByElectionId(electionId);
    }
    @GetMapping("/not-in-election/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CandidateDTO> getCandidatesNotInElection(@PathVariable Long electionId){
        return candidateService.getCandidatesNotInElection(electionId);
    }

    @GetMapping("/user-vote/{userId}")
    public List<CandidateDTO> getCandidatesByUser(@PathVariable Long userId){
        return candidateService.getCandidatesByUserVote(userId);
    }

    @GetMapping(value = "/{id}")
    public CandidateDTO getCandidateById(@PathVariable Long id){
        return candidateService.findCandidateById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public CandidateDTO createCandidate(@Valid @RequestBody CandidateDTO candidate){
        return candidateService.createCandidate(candidate);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CandidateDTO updateCandidate(@Valid @RequestBody CandidateDTO candidate, @PathVariable Long id){
        return candidateService.updateCandidate(candidate,id);
    }
    @PutMapping(value = "/add-election/{candidateId}/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CandidateDTO addElection(@PathVariable Long candidateId, @PathVariable Long electionId){
        return candidateService.addElectionCandidate(candidateId,electionId);
    }
    @DeleteMapping(value = "/remove-election/{candidateId}/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> removeElection(@PathVariable Long electionId, @PathVariable Long candidateId){
        candidateService.deleteElectionCandidate(candidateId,electionId);
        return ResponseEntity.ok().body("Xóa thành công");
    }
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id){
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    @PatchMapping("/upload-image/{candidateId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<CandidateDTO> uploadImage(@PathVariable Long candidateId, @RequestParam("image") MultipartFile image) throws IOException {
        return ApiResponse.<CandidateDTO>builder()
                .code(200)
                .message("Cập nhật ảnh thành công")
                .result(candidateService.updateImage(candidateId,image))
                .build();
    }

}

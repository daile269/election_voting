package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.ElectionCandidateDTO;
import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.dto.request.AddCandidatesRequest;
import com.datn.electronic_voting.dto.request.UsersRequest;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.PaginatedResponse;
import com.datn.electronic_voting.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/elections")
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping(value = "/paginated")
    public PaginatedResponse<ElectionDTO> getElectionList(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);

        return PaginatedResponse.<ElectionDTO>builder()
                .listElements(electionService.getElectionPageable(pageable))
                .totalPages((int) Math.ceil( (double) (electionService.totalItem())/size))
                .build();

    }
    @GetMapping("/filter")
    public ResponseEntity<Page<ElectionDTO>> searchElections(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Page<ElectionDTO> result = electionService.searchElections(search, status, page, size);
        return ResponseEntity.ok(result);
    }
    @GetMapping
    public List<ElectionDTO> getAllElection(){
        return electionService.getAllElections();
    }

    @GetMapping(value = "/{id}")
    public ElectionDTO getElectionById(@PathVariable Long id){
        return electionService.findElectionById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ElectionDTO createElection(@Valid @RequestBody ElectionDTO election){
        return electionService.createElection(election);
    }

    @GetMapping(value = "/candidate/{candidateId}")
    public List<ElectionDTO> getElectionListByCandidate(@PathVariable Long candidateId){
        return electionService.getElectionByCandidateId(candidateId);
    }

    @GetMapping(value = "/user/{userId}/paginated")
    public PaginatedResponse<ElectionDTO> getElectionForUser(@PathVariable Long userId
            ,@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return PaginatedResponse.<ElectionDTO>builder()
                .listElements(electionService.getElectionByUserId(userId,pageable))
                .totalPages((int) Math.ceil( (double) (electionService.totalItemElectionsForUser(userId))/size))
                .build();

    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ElectionDTO updateElection(@Valid @RequestBody ElectionDTO election, @PathVariable Long id){
        return electionService.updateElection(election,id);
    }

    @PutMapping(value = "/add-users/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse addUsersToElection(@PathVariable Long electionId, @RequestBody UsersRequest request){
        electionService.addUsersToElection(electionId, request.getUserIds());
        return ApiResponse.builder()
                .code(200)
                .message("Thêm thành công danh sách user")
                .build();
    }
    @DeleteMapping(value = "/remove-users/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse removeUsersToElection(@PathVariable Long electionId, @RequestBody UsersRequest request){
        electionService.deleteUsersToElection(electionId, request.getUserIds());
        return ApiResponse.builder()
                .code(200)
                .message("Xóa thành công danh sách user")
                .build();
    }
    @PutMapping(value = "/add-candidate/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse addCandidateElection(@PathVariable Long electionId, @RequestBody AddCandidatesRequest request){
        electionService.addCandidateElection(electionId, request.getCandidateIds());
        return ApiResponse.builder()
                .code(200)
                .message("Thêm thành công danh sách ứng viên")
                .build();
    }
    @DeleteMapping(value = "/remove-candidate/{electionId}/{candidateId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse removeCandidateElection(@PathVariable Long electionId,@PathVariable Long candidateId){
        electionService.deleteCandidateElection(electionId,candidateId);
        return ApiResponse.builder()
                .code(200)
                .message("Xóa thành công ứng viên")
                .build();
    }

    @GetMapping("/{electionId}/candidates")
    public List<ElectionCandidateDTO> getResultElection(@PathVariable Long electionId){
        return electionService.getCandidatesByElectionId(electionId);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteElection(@PathVariable Long id){
        electionService.deleteElection(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }
    @GetMapping("/joinElection/{electionCode}")
    public ElectionDTO findElectionByElectionCode(@PathVariable String electionCode){
        return electionService.findElectionByElectionCode(electionCode);
    }
}

package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.PaginatedResponse;
import com.datn.electronic_voting.service.VoteService;
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
@RequestMapping("api/votes")
public class VoteController {

    private final VoteService voteService;

    @GetMapping
    public List<VoteDTO> getVotes(){
        return voteService.getAllVotes();
    }

    @GetMapping(value = "/paginated")
    public PaginatedResponse<VoteDTO> getVotePageable(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return PaginatedResponse.<VoteDTO>builder()
                .listElements(voteService.getVotesPageable(pageable))
                .totalPages((int) Math.ceil( (double) (voteService.totalItem())/size))
                .build();
    }
    @GetMapping("/filter")
    public ResponseEntity<Page<VoteDTO>> getVotesPaginated(
            @RequestParam(required = false) Long electionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<VoteDTO> result = voteService.getVotesAndFilter(electionId, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/election/{electionId}")
    public List<VoteDTO> getVotesByElection(@PathVariable Long electionId){
        return voteService.getVotesAndFilter(electionId);
    }

    @GetMapping("/user/{userId}/paginated")
    public PaginatedResponse<VoteDTO> getVotesForUser(@PathVariable Long userId,@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return PaginatedResponse.<VoteDTO>builder()
                .listElements(voteService.getVoteByUserId(userId,pageable))
                .totalPages((int) Math.ceil( (double) (voteService.totalItemVotesForUser(userId))/size))
                .build();
    }
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public VoteDTO getVoteById(@PathVariable Long id){
        return voteService.findVoteById(id);
    }

    @PostMapping
    public ApiResponse<VoteDTO> createVote(@RequestBody VoteDTO vote, @RequestParam boolean voteChoice){
        return ApiResponse.<VoteDTO>builder()
                .code(200)
                .message("Bạn đã bỏ phiếu thành công cho ứng viên này")
                .result(voteService.createVote(vote,voteChoice))
                .build();

    }

    @PutMapping(value = "/{id}")
    public VoteDTO updateVote(@RequestBody VoteDTO vote, @PathVariable Long id,@RequestParam boolean voteChoice){
        return voteService.updateVote(vote,id,voteChoice);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteVote(@PathVariable Long id){
        voteService.deleteVote(id);
        return ResponseEntity.ok("Xóa thành công");
    }

    @GetMapping("/agreeVote/{electionId}/{candidateId}")
    public ResponseEntity<String> countAgreeVotes(@PathVariable Long electionId,@PathVariable Long candidateId){
        return ResponseEntity.ok("Tổng số lượng vote đồng ý cho ứng viên này là: "
                +voteService.countAgreeVotes(electionId,candidateId));
    }
    @GetMapping("/{electionId}/{candidateId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<VoteDTO> getvoteEle(@PathVariable Long electionId,@PathVariable Long candidateId){
        return voteService.getVoteByElectionAndCandidateId(electionId,candidateId);
    }

    @GetMapping("/totalVote/{electionId}/{candidateId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> totalVoteInElection(@PathVariable Long electionId,@PathVariable Long candidateId){
        return ResponseEntity.ok("Tổng số lượng vote cho ứng viên này là: "
                +voteService.countVoteCandidateInElection(electionId,candidateId));
    }
}

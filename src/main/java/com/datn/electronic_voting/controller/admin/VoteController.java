package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.dto.response.PaginatedResponse;
import com.datn.electronic_voting.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/election/{electionId}")
    public List<VoteDTO> getVotesByElection(@PathVariable Long electionId){
        return voteService.getVotesByElectionId(electionId);
    }
    @GetMapping(value = "/{id}")
    public VoteDTO getVoteById(@PathVariable Long id){
        return voteService.findVoteById(id);
    }

    @PostMapping
    public VoteDTO createVote(@RequestBody VoteDTO vote, @RequestParam boolean voteChoice){
        return voteService.createVote(vote,voteChoice);
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
    public List<VoteDTO> getvoteEle(@PathVariable Long electionId,@PathVariable Long candidateId){
        return voteService.getVoteByElectionAndCandidateId(electionId,candidateId);
    }

    @GetMapping("/totalVote/{electionId}/{candidateId}")
    public ResponseEntity<String> totalVoteInElection(@PathVariable Long electionId,@PathVariable Long candidateId){
        return ResponseEntity.ok("Tổng số lượng vote cho ứng viên này là: "
                +voteService.countVoteCandidateInElection(electionId,candidateId));
    }
}

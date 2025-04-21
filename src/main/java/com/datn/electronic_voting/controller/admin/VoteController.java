package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.entity.Vote;
import com.datn.electronic_voting.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/votes")
public class VoteController {

    private final VoteService voteService;

    @GetMapping
    public List<Vote> getVotes(){
        return voteService.getAllVotes();
    }

    @GetMapping(value = "/paginated")
    public List<Vote> getVotePageable(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return voteService.getVotesPageable(pageable);
    }

    @GetMapping(value = "/{id}")
    public Vote getVoteById(@PathVariable Long id){
        return voteService.findVoteById(id);
    }

    @PostMapping
    public Vote createVote(@RequestBody Vote vote, @RequestParam boolean voteChoice){
        return voteService.createVote(vote,voteChoice);
    }

    @PutMapping(value = "/{id}")
    public Vote updateVote(@RequestBody Vote vote, @PathVariable Long id,@RequestParam boolean voteChoice){
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
    public List<Vote> getvoteEle(@PathVariable Long electionId,@PathVariable Long candidateId){
        return voteService.getVoteByElectionAndCandidateId(electionId,candidateId);
    }

    @GetMapping("/totalVote/{electionId}/{candidateId}")
    public ResponseEntity<String> totalVoteInElection(@PathVariable Long electionId,@PathVariable Long candidateId){
        return ResponseEntity.ok("Tổng số lượng vote cho ứng viên này là: "
                +voteService.countVoteCandidateInElection(electionId,candidateId));
    }
}

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
    public Vote createVote(@RequestBody Vote vote){
        return voteService.createVote(vote);
    }

    @PutMapping(value = "/{id}")
    public Vote updateVote(@RequestBody Vote vote, @PathVariable Long id){
        return voteService.updateVote(vote,id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteVote(@PathVariable Long id){
        voteService.deleteVote(id);
        return ResponseEntity.ok("Xóa thành công");
    }

}

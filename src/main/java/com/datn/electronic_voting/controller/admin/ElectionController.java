package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/elections")
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping(value = "/paginated")
    public List<Election> getElectionList(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return electionService.getElectionPageable(pageable);
    }

    @GetMapping
    public List<Election> getAllElection(){
        return electionService.getAllElections();
    }

    @GetMapping(value = "/{id}")
    public Election getElectionById(@PathVariable Long id){
        return electionService.findElectionById(id);
    }

    @PostMapping
    public Election createElection(@RequestBody Election election){
        return electionService.createElection(election);
    }

    @PutMapping(value = "/{id}")
    public Election updateElection(@RequestBody Election election,@PathVariable Long id){
        return electionService.updateElection(election,id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteElection(@PathVariable Long id){
        electionService.deleteElection(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }
}

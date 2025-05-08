package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.ElectionDTO;
import com.datn.electronic_voting.dto.response.PaginatedResponse;
import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<ElectionDTO> getAllElection(){
        return electionService.getAllElections();
    }

    @GetMapping(value = "/{id}")
    public ElectionDTO getElectionById(@PathVariable Long id){
        return electionService.findElectionById(id);
    }

    @PostMapping
    public ElectionDTO createElection(@Valid @RequestBody ElectionDTO election){
        return electionService.createElection(election);
    }

    @PutMapping(value = "/{id}")
    public ElectionDTO updateElection(@Valid @RequestBody ElectionDTO election, @PathVariable Long id){
        return electionService.updateElection(election,id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteElection(@PathVariable Long id){
        electionService.deleteElection(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }
    @GetMapping("/joinElection/{electionCode}")
    public ElectionDTO findElectionByElectionCode(@PathVariable String electionCode){

        return electionService.findElectionByElectionCode(electionCode);
    }
}

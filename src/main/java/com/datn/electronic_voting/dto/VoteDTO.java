package com.datn.electronic_voting.dto;

import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDTO {
    private Long id;

    private Long userId;

    private Long electionId;

    private Long candidateId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime voteTime;

    private String gx;

    private String encryptedVote;

    private String gy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;


}

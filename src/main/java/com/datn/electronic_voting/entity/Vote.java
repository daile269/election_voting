package com.datn.electronic_voting.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@Table(name = "vote",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "userId", "electionId" }) })
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "userId",nullable = false,insertable = false,updatable = false)
    private User userVote;
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "electionId",nullable = false,insertable = false,updatable = false)
    private Election electionVote;
    private Long electionId;

    @ManyToOne
    @JoinColumn(name = "candidateId",nullable = false,insertable = false,updatable = false)
    private Candidate candidateVote;
    private Long candidateId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime voteTime;

    @PrePersist
    public void prePersist() {
        this.voteTime = LocalDateTime.now();
    }

}

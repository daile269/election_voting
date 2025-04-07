package com.datn.electronic_voting.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@Table(name = "result",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"electionId", "candidateId"})
        })
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "candidateId",nullable = false,updatable = false,insertable = false)
    private Candidate candidateResult;
    private Long candidateId;

    @ManyToOne
    @JoinColumn(name = "electionId",nullable = false,updatable = false,insertable = false)
    private Election electionResult;
    private Long electionId;

    private int voteCount;

    private boolean isWinner;
}

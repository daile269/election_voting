package com.datn.electronic_voting.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectionCandidateId implements Serializable {
    private Long electionId;
    private Long candidateId;
}

package com.datn.electronic_voting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "election_candidate")
public class ElectionCandidate {

    @EmbeddedId
    private ElectionCandidateId id = new ElectionCandidateId();

    @ManyToOne
    @MapsId("electionId")
    @JoinColumn(name = "election_id")
    @ToString.Exclude
    private Election election;

    @ManyToOne
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    @ToString.Exclude
    private Candidate candidate;

    private int voteCount = 0;
}

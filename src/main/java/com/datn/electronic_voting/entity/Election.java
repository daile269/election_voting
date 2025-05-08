package com.datn.electronic_voting.entity;

import com.datn.electronic_voting.enums.ElectronStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@Table(name = "election")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String electionCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;


    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ElectronStatus status;


    @PrePersist
    public void prePersist() {
        if(this.startTime == null) this.startTime = LocalDateTime.now();
        String uuidNumbers = UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "");
        this.electionCode= uuidNumbers.substring(0, 10);
        if (status == null) {
            status = ElectronStatus.ONGOING;
        }
    }

    @OneToMany(mappedBy = "electionVote")
    private List<Vote> voteList;

    @OneToMany(mappedBy = "election")
    private List<Candidate> candidateList;

    @OneToMany(mappedBy = "electionResult")
    private List<Result> resultList;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}

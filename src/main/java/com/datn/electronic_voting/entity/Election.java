package com.datn.electronic_voting.entity;

import com.datn.electronic_voting.enums.ElectronStatus;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;


    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
}

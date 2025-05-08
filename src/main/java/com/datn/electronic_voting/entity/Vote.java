package com.datn.electronic_voting.entity;

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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    // gx = g^x mod p (public key của người vote)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String gx;

    // encryptedVote = (g^y)^x * g mod p (phiếu mã hóa)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedVote;

    // encryptedVote = (g^y)^x * g mod p (phiếu mã hóa)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String gy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
    @PrePersist
    public void prePersist() {
        this.voteTime = LocalDateTime.now();
    }

}

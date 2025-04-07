package com.datn.electronic_voting.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@Table(name = "candidate")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "electionId",nullable = false,insertable = false,updatable = false)
    private Election election;
    private Long electionId;

    @Column(nullable = false, length = 255)
    private String fullName;

    @Column(unique = true,nullable = false, length = 255)
    private String email;
    private String phone;
    @Column(nullable = false,length = 500)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private String address;
    private String urlAvatar;

    @OneToMany(mappedBy = "candidateVote")
    private List<Vote> voteList;

    @OneToOne(mappedBy = "candidateResult")
    private Result result;
}

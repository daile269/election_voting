package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.Candidate;
import com.datn.electronic_voting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);

    User findByEmail(String email);
    boolean existsByEmail(String email);


    @Query(value = "SELECT * FROM user c WHERE c.id NOT IN " +
            "(SELECT ue.user_id FROM user_election ue " +
            "WHERE ue.election_id = :electionId)", nativeQuery = true)
    List<User> findUserNotInElection(@Param("electionId") Long electionId);

    @Query(value = "SELECT * FROM user c WHERE c.id IN " +
            "(SELECT ue.user_id FROM user_election ue " +
            "WHERE ue.election_id = :electionId)", nativeQuery = true)
    List<User> findUserInElection(@Param("electionId") Long electionId);

    @Query(value = "SELECT COUNT(*) FROM user c WHERE c.id IN " +
            "(SELECT ue.user_id FROM user_election ue " +
            "WHERE ue.election_id = :electionId)", nativeQuery = true)
    int countUserInElection(@Param("electionId") Long electionId);
}

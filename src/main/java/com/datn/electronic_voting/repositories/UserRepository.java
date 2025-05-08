package com.datn.electronic_voting.repositories;

import com.datn.electronic_voting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);

    boolean existsByEmail(String email);
}

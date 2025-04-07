package com.datn.electronic_voting.service;

import com.datn.electronic_voting.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user, Long id);

    List<User> getAllUsers();

    List<User> getUsersPageable(Pageable pageable);

    User findUserById(Long id);

    void deleteUser(Long id);
}

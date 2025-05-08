package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.dto.request.AuthenticationRequest;
import com.datn.electronic_voting.dto.response.AuthenticationResponse;
import com.datn.electronic_voting.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO, Long id);

    List<UserDTO> getAllUsers();

    List<UserDTO> getUsersPageable(Pageable pageable);

    UserDTO findUserById(Long id);

    void deleteUser(Long id);

    AuthenticationResponse verify(AuthenticationRequest auth);

    UserDTO registerUser(UserDTO userDTO);

    UserDTO updateImage(Long candidateId,MultipartFile image) throws IOException;

    void activeUser(Long userId, String verifyCode);

    int totalItem();
}

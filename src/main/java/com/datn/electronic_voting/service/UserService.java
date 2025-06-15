package com.datn.electronic_voting.service;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.dto.request.*;
import com.datn.electronic_voting.dto.response.LoginResponse;
import org.springframework.data.domain.Page;
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

    UserDTO findUserByUsername(String username);

    List<UserDTO> findUserInElection(Long electionId);

    List<UserDTO> findUserNotInElection(Long electionId);
    Page<UserDTO> searchUsersPaginated(int page, int size, String fullName, String username, String email, String role);

    void deleteUser(Long id);

    LoginResponse verify(AuthenticationRequest auth);

    UserDTO registerUser(UserDTO userDTO);

    UserDTO updateImage(Long candidateId,MultipartFile image) throws IOException;

    void verifyUser(VerifyCodeRequest request);

    void resetPassword(ResetPasswordRequest request);

    void sendVerifyCode(SendVerifyCodeRequest request);

    void changePassword(ChangePasswordRequest request);
    int totalItem();
}

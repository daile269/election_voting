package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.dto.request.*;
import com.datn.electronic_voting.dto.response.LoginResponse;
import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.enums.Role;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.UserMapper;
import com.datn.electronic_voting.repositories.UserRepository;
import com.datn.electronic_voting.service.UserService;
import com.datn.electronic_voting.untils.S3Service;
import com.datn.electronic_voting.untils.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final EmailServiceImpl emailServiceImpl;

    private final S3Service s3Service;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        checkUserExist(user);
        user.setActive(true);
        user.setPassword(encoder.encode(user.getPassword()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO,Long id) {
        User user = userMapper.toEntity(userDTO);
        User user1 = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        user.setId(id);
        if(!user.getPassword().equals(user1.getPassword())){
            user.setPassword(encoder.encode(user.getPassword()));
        }else user.setPassword(user.getPassword());
        user.setUrlAvatar(user1.getUrlAvatar());
        user.setEmail(user1.getEmail());
        if(user.getRole()==null){
            user.setRole(user1.getRole());
        }
        user.setCreatedAt(user1.getCreatedAt());
        user.setCreatedBy(user1.getCreatedBy());
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream().map(user -> userMapper.toDTO(user)).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersPageable(Pageable pageable) {
        return userRepository.findAll(pageable).getContent()
                .stream().map(user -> userMapper.toDTO(user)).collect(Collectors.toList());
    }

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO findUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> findUserInElection(Long electionId) {
        List<User> users = userRepository.findUserInElection(electionId);
        return users.stream()
                .map(user -> userMapper.toDTO(user)).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findUserNotInElection(Long electionId) {
        List<User> users = userRepository.findUserNotInElection(electionId);
        List<User> filteredUsers = users.stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN)&& user.isActive())
                .collect(Collectors.toList());
        return filteredUsers.stream()
                .map(user -> userMapper.toDTO(user)).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        userRepository.deleteById(id);
    }

    @Override
    public LoginResponse verify(AuthenticationRequest auth) {
        User user = userRepository.findByUsername(auth.getUsername());
        if(user == null || !encoder.matches(auth.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.LOGIN_VALID);
        }
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(auth.getUsername(),auth.getPassword()));
        if(!authentication.isAuthenticated()) throw new AppException(ErrorCode.UNAUTHENTICATED);
        String tokenResponse = jwtService.generateToken(auth.getUsername());
        return LoginResponse.builder()
                .token(tokenResponse)
                .authenticated(true).build();
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        checkUserExist(user);
        user.setPassword(encoder.encode(user.getPassword()));
        String verifyCode = String.valueOf(100000 + new Random().nextInt(900000));
        emailServiceImpl.sendVerificationEmail(user.getEmail(),verifyCode);
        user.setVerifyCode(verifyCode);
        user.setActive(false);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateImage(Long userId,MultipartFile image) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        String urlImage = s3Service.uploadFile(image);
        user.setUrlAvatar(urlImage);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void verifyUser(VerifyCodeRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user==null) throw new AppException(ErrorCode.USER_IS_NOT_EXISTS);
        if(user.isActive()) throw new AppException(ErrorCode.USER_IS_ACTIVE);
        if(user.getVerifyCode().equals(request.getVerifyCode())){
            user.setActive(true);
            userRepository.save(user);
        }else throw new AppException(ErrorCode.VERIFY_CODE_VALID);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if(user==null) throw new AppException(ErrorCode.USER_IS_NOT_EXISTS);
        String newPassword = generatePassword();
        user.setPassword(encoder.encode(newPassword));
        emailServiceImpl.sendNewPasswordToEmail(request.getEmail(), newPassword,user.getUsername());
        userRepository.save(user);
    }

    @Override
    public void sendVerifyCode(SendVerifyCodeRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user==null) throw new AppException(ErrorCode.USER_IS_NOT_EXISTS);
        String verifyCode = String.valueOf(100000 + new Random().nextInt(900000));
        user.setVerifyCode(verifyCode);
        emailServiceImpl.sendVerificationEmail(user.getEmail(),verifyCode);
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user==null) throw new AppException(ErrorCode.USER_IS_NOT_EXISTS);
        if(encoder.matches(request.getOldPassword(),user.getPassword())){
            user.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(user);
        }else throw new AppException(ErrorCode.PASS_NOT_MATCH);
    }



    @Override
    public int totalItem() {
        return (int) userRepository.count();
    }

    private void checkUserExist(User user){
        User checkUser = userRepository.findByUsername(user.getUsername());
        if(checkUser!=null) throw new AppException(ErrorCode.USER_IS_EXISTS);
        if (userRepository.existsByEmail(user.getEmail()))
            throw new AppException(ErrorCode.EMAIL_IS_EXISTS);
    }

    public String generatePassword(){
         String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=";
         int PASSWORD_LENGTH = 12;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

}

package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.request.AuthenticationRequest;
import com.datn.electronic_voting.dto.response.AuthenticationResponse;
import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.UserRepository;
import com.datn.electronic_voting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IUserService implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    @Override
    public User createUser(User user) {
        checkUserExist(user);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user,Long id) {
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        user.setId(id);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersPageable(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        userRepository.deleteById(id);
    }

    @Override
    public AuthenticationResponse verify(AuthenticationRequest auth) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(auth.getUsername(),auth.getPassword()));
        if(!authentication.isAuthenticated()) throw new AppException(ErrorCode.UNAUTHENTICATED);
        String tokenResponse = jwtService.generateToken(auth.getUsername());
        return AuthenticationResponse.builder()
                .token(tokenResponse)
                .authenticated(true).build();
    }

    private void checkUserExist(User user){
        User checkUser = userRepository.findByUsername(user.getUsername());
        if(checkUser!=null) throw new AppException(ErrorCode.USER_IS_EXISTS);
    }
}

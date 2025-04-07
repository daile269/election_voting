package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.entity.UserPrincipal;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new AppException(ErrorCode.USER_IS_NOT_EXISTS);
        return new UserPrincipal(user);
    }
}

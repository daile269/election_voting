package com.datn.electronic_voting.controller.auth;

import com.datn.electronic_voting.dto.request.AuthenticationRequest;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.AuthenticationResponse;
import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.service.impl.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping()
public class AuthenticationController {

    private final IUserService userService;


    @PostMapping(value = "/register")
    @ResponseBody
    private ApiResponse register(@RequestBody User user){
        userService.createUser(user);
        return ApiResponse.builder().code(200).message("Đăng ký thành công").build();
    }

    @PostMapping(value = "/auth/login")
    @ResponseBody
    public AuthenticationResponse login(@RequestBody AuthenticationRequest auth){
        return userService.verify(auth);
    }



}

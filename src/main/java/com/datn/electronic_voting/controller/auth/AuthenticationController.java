package com.datn.electronic_voting.controller.auth;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.AuthenticationResponse;
import com.datn.electronic_voting.dto.request.AuthenticationRequest;
import com.datn.electronic_voting.dto.request.VerifyCodeRequest;
import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class AuthenticationController {

    private final UserServiceImpl userService;


    @PostMapping(value = "/register")
    private ApiResponse<UserDTO> register(@Valid @RequestBody UserDTO user){
        return ApiResponse.<UserDTO>builder()
                .code(200)
                .result(userService.registerUser(user))
                .message("Đăng ký thành công").build();
    }

    @PostMapping(value = "/auth/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest auth){
        return userService.verify(auth);
    }

    @PatchMapping(value = "/auth/activeUser/{userId}")
    public ApiResponse<String> activeUser(@PathVariable Long userId,@RequestBody VerifyCodeRequest request){
        userService.activeUser(userId,request.getVerifyCode());
        return ApiResponse.<String>builder().code(200).message("Xác thực người dùng thành công").build();
    }

}

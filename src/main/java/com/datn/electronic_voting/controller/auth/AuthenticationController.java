package com.datn.electronic_voting.controller.auth;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.dto.request.AuthenticationRequest;
import com.datn.electronic_voting.dto.request.SendVerifyCodeRequest;
import com.datn.electronic_voting.dto.request.VerifyCodeRequest;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.LoginResponse;
import com.datn.electronic_voting.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api")
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
    public LoginResponse login(@RequestBody AuthenticationRequest auth){
        return userService.verify(auth);
    }

    @PatchMapping(value = "/auth/verifyUser")
    public ApiResponse<String> verifyUser(@RequestBody VerifyCodeRequest request){
        userService.verifyUser(request);
        return ApiResponse.<String>builder().code(200).message("Xác thực người dùng thành công").build();
    }
    @PostMapping("/send-verify-code")
    public ApiResponse sendVerifyCode(@RequestBody SendVerifyCodeRequest request) {
        userService.sendVerifyCode(request);
        return ApiResponse.builder()
                .code(200)
                .message("Mã xác thực đã được gửi về email của bạn")
                .build();
    }


}

package com.datn.electronic_voting.dto.response;

import com.datn.electronic_voting.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    String token;
    boolean authenticated;
}

package com.datn.electronic_voting.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UsersRequest {
    private List<Long> userIds;
}

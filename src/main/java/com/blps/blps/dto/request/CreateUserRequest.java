package com.blps.blps.dto.request;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String role;
    private Long referenceId;
}
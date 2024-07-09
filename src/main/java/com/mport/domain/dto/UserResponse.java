package com.mport.domain.dto;

import lombok.Data;

import java.security.AuthProvider;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private AuthProvider authProvider;
    private String name;
    private String imageUrl;
}

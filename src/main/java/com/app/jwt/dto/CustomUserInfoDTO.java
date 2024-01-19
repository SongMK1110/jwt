package com.app.jwt.dto;

import lombok.Data;

@Data
public class CustomUserInfoDTO {
    private Long memberId;
    private String email;
    private String name;
    private String pw;
    private String role;
}

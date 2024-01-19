package com.app.jwt.dto.request;

import lombok.Data;

@Data
public class RequestSignUpDTO {
    private String email;
    private String pw;
    private String name;
}

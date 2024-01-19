package com.app.jwt.dto.request;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

@Data
public class RequestMemberDTO {
    private String email;

    private String pw;
}

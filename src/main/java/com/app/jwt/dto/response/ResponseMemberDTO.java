package com.app.jwt.dto.response;

import lombok.Data;

@Data
public class ResponseMemberDTO {
    private Long memberId;
    private String email;
    private String name;
    private String pw;
    private String role;
}

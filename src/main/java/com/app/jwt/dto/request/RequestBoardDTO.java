package com.app.jwt.dto.request;

import lombok.Data;

@Data
public class RequestBoardDTO {
    private String title;
    private String writer;
    private String content;
}

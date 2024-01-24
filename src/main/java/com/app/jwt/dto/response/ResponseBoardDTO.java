package com.app.jwt.dto.response;

import lombok.Data;

@Data
public class ResponseBoardDTO {
    private long boardId;
    private String title;
    private String writer;
    private String content;
}

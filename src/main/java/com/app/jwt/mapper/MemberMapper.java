package com.app.jwt.mapper;

import com.app.jwt.dto.request.RequestBoardDTO;
import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.dto.CustomUserInfoDTO;
import com.app.jwt.dto.response.ResponseBoardDTO;

import java.util.List;

public interface MemberMapper {

    CustomUserInfoDTO login(RequestMemberDTO requestMemberDTO);
    int signUp(RequestSignUpDTO requestSignUpDTO);
    CustomUserInfoDTO findById(long id);
    List<ResponseBoardDTO> boardList();
    int insertBoard(RequestBoardDTO requestBoardDTO);
    void updateRefreshToken(String refreshToken, String email);
    String selectRefreshToken(long memberId);
}

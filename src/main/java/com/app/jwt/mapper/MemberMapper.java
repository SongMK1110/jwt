package com.app.jwt.mapper;

import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.dto.CustomUserInfoDTO;

public interface MemberMapper {

    CustomUserInfoDTO login(RequestMemberDTO requestMemberDTO);
    int signUp(RequestSignUpDTO requestSignUpDTO);
    CustomUserInfoDTO findById(long id);
}

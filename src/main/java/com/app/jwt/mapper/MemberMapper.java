package com.app.jwt.mapper;

import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.dto.response.ResponseMemberDTO;

public interface MemberMapper {

    ResponseMemberDTO login(RequestMemberDTO requestMemberDTO);

    int signUp(RequestSignUpDTO requestSignUpDTO);
}

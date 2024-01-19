package com.app.jwt.service;

import com.app.jwt.common.jwt.JwtUtil;
import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.dto.CustomUserInfoDTO;
import com.app.jwt.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberMapper memberMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String login(RequestMemberDTO requestMemberDTO) {
        String email = requestMemberDTO.getEmail();
        String password = requestMemberDTO.getPw();
        CustomUserInfoDTO member = memberMapper.login(requestMemberDTO);
        if(member == null) {
            throw new UsernameNotFoundException("이메일이 존재하지 않습니다.");
        }

//         암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        if(!passwordEncoder.matches(password, member.getPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDTO customUserInfoDTO = new CustomUserInfoDTO();
        BeanUtils.copyProperties(member, customUserInfoDTO);

        return jwtUtil.createAccessToken(customUserInfoDTO);
    }

    @Transactional
    public int signUp(RequestSignUpDTO requestSignUpDTO) {
        requestSignUpDTO.setPw(passwordEncoder.encode(requestSignUpDTO.getPw()));
        log.info("회원가입 정보 : " + requestSignUpDTO);
        return memberMapper.signUp(requestSignUpDTO);
    }
}

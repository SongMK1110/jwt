package com.app.jwt.service;

import com.app.jwt.common.jwt.JwtUtil;
import com.app.jwt.dto.request.RequestBoardDTO;
import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.dto.CustomUserInfoDTO;
import com.app.jwt.dto.response.ResponseBoardDTO;
import com.app.jwt.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberMapper memberMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, String> login(RequestMemberDTO requestMemberDTO) {
        String email = requestMemberDTO.getEmail();
        String password = requestMemberDTO.getPw();
        CustomUserInfoDTO member = memberMapper.login(requestMemberDTO);
        Map<String, String> tokens = new HashMap<>();
        if (member == null) {
            tokens.put("accessToken", "No User");
            return tokens;
//            throw new UsernameNotFoundException("No User");
        }

        if (!passwordEncoder.matches(password, member.getPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(member);
        String refreshToken = jwtUtil.createRefreshToken(member);

        memberMapper.updateRefreshToken(refreshToken, member.getEmail());

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }


    @Transactional
    public int signUp(RequestSignUpDTO requestSignUpDTO) {
        requestSignUpDTO.setPw(passwordEncoder.encode(requestSignUpDTO.getPw()));
        log.info("회원가입 정보 : " + requestSignUpDTO);
        return memberMapper.signUp(requestSignUpDTO);
    }

    public List<ResponseBoardDTO> boardList() {
        return memberMapper.boardList();
    }

    @Transactional
    public int insertBoard(RequestBoardDTO requestBoardDTO) {
        return memberMapper.insertBoard(requestBoardDTO);
    }
}

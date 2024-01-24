package com.app.jwt.controller;

import com.app.jwt.common.jwt.CustomUserDetailService;
import com.app.jwt.common.jwt.CustomUserDetails;
import com.app.jwt.common.jwt.JwtUtil;
import com.app.jwt.dto.CustomUserInfoDTO;
import com.app.jwt.dto.request.RequestBoardDTO;
import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.dto.response.ResponseBoardDTO;
import com.app.jwt.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/sign-up-form")
    public String signUpForm() {
        return "signUp";
    }

    @PostMapping("/refresh")
    @ResponseBody
    public String refreshAccessToken(@CookieValue("RefreshToken") String refreshToken, HttpServletResponse response) {
        String token = refreshToken.substring(7);

        if (jwtUtil.isTokenExpired(token)) {
            return "refreshToken-expired";
        }

        if (jwtUtil.validateToken(token).equals("success")) {
            String id = String.valueOf(jwtUtil.getUserId(token));
            UserDetails userDetails = customUserDetailService.loadUserByUsername(id);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            CustomUserInfoDTO customUserInfoDTO = customUserDetails.getMember();
            String newAccessToken = "Bearer+" + jwtUtil.createAccessToken(customUserInfoDTO);

            Cookie cookie = new Cookie("Authorization", newAccessToken);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
            return newAccessToken;
        } else {
            return "Invalid Refresh Token";
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, String> login(@RequestBody RequestMemberDTO requestMemberDTO, HttpServletResponse response) {
        Map<String, String> map = memberService.login(requestMemberDTO);
        String accessToken = "Bearer+" +  map.get("accessToken");
        Cookie cookie = new Cookie("Authorization", accessToken);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        String refreshToken = "Bearer+" +  map.get("refreshToken");
        cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        return map;
    }

    @PostMapping("/sign-up")
    public String signUp(RequestSignUpDTO requestSignUpDTO) {
        if (memberService.signUp(requestSignUpDTO) <= 0) {
            return "fail";
        }
        return "redirect:/login-form";
    }

    @GetMapping("/board")
    @ResponseBody
    public List<ResponseBoardDTO> boardList() {
        return memberService.boardList();
    }

    @PostMapping("/insert-board")
    @ResponseBody
    public String insertBoard(@RequestBody RequestBoardDTO requestBoardDTO, @CookieValue("Authorization") String authorization) {

        String token = authorization.substring(7);

        if (jwtUtil.isTokenExpired(token)) {
            return "token-expired";
        }

        // 토큰이 유효한 경우에만 아래의 코드가 실행됩니다.
        String username = jwtUtil.parseClaims(token).get("name", String.class);
        requestBoardDTO.setWriter(username);

        if (memberService.insertBoard(requestBoardDTO) <= 0) {
            return "fail";
        }
        return "success";
    }


}

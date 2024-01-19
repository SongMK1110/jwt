package com.app.jwt.controller;

import com.app.jwt.dto.request.RequestMemberDTO;
import com.app.jwt.dto.request.RequestSignUpDTO;
import com.app.jwt.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody RequestMemberDTO requestMemberDTO) {
        log.info("request : " + requestMemberDTO);
        return memberService.login(requestMemberDTO);
    }

    @PostMapping("/sign-up")
    public String signUp(RequestSignUpDTO requestSignUpDTO) {
        if (memberService.signUp(requestSignUpDTO) <= 0) {
            return "fail";
        }
        return "redirect:/login-form";
    }
}

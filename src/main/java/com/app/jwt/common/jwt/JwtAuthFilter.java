package com.app.jwt.common.jwt;

import com.app.jwt.dto.CustomUserInfoDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailService customUserDetailService;
    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Cookie[] cookies = request.getCookies();
        String refreshHeader = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("RefreshToken".equals(cookie.getName())) {
                    refreshHeader = cookie.getValue();
                    break;
                }
            }
        }

        // JWT가 헤더에 있는 경우
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer+")) {
            String accessToken = authorizationHeader.substring(7);

            // AccessToken 유효성 검증
            if (jwtUtil.validateToken(accessToken).equals("success")) {
                Long userId = jwtUtil.getUserId(accessToken);

                // 유저와 토큰 일치 시 userDetails 생성
                UserDetails userDetails = customUserDetailService.loadUserByUsername(userId.toString());

                if (userDetails != null) {
                    // UserDetails, Password, Role -> 접근권한 인증 Token 생성
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 현재 Request의 Security Context에 접근권한 설정
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            } else if (jwtUtil.validateToken(accessToken).equals("expired accessToken") && refreshHeader != null && refreshHeader.startsWith("Bearer+")) {
                log.info("Access token expired. Refreshing...");

                String refreshToken = refreshHeader.substring(7);

                // RefreshToken 유효성 검증
                if (jwtUtil.validateToken(refreshToken).equals("success")) {
                    Long userId = jwtUtil.getUserId(refreshToken);
                    UserDetails userDetails = customUserDetailService.loadUserByUsername(userId.toString());
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                    CustomUserInfoDTO customUserInfoDTO = customUserDetails.getMember();
                    log.info("customUserInfoDTO : " + customUserInfoDTO);
                    if (customUserInfoDTO != null) {
                        // 새로운 AccessToken 생성
                        String newAccessToken = jwtUtil.createAccessToken(customUserInfoDTO);

                        // Response 헤더에 새로운 AccessToken 추가
                        response.addCookie(new Cookie("Authorization", "Bearer+" + newAccessToken));
//                        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

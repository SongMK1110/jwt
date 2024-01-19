package com.app.jwt.common.jwt;

import com.app.jwt.dto.CustomUserInfoDTO;
import com.app.jwt.mapper.MemberMapper;
import com.app.jwt.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {

    private final MemberMapper memberMapper;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        CustomUserInfoDTO member = memberMapper.findById(Long.parseLong(id));
        if(member == null) {
            throw new UsernameNotFoundException("해당하는 유저가 없습니다.");
        }

        return new CustomUserDetails(member);
    }
}

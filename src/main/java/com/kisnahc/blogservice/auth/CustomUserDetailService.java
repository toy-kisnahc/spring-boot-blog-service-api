package com.kisnahc.blogservice.auth;

import com.kisnahc.blogservice.domain.Member;

import com.kisnahc.blogservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public MemberAdapter loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("member not found"));
        return new MemberAdapter(member);
    }
}

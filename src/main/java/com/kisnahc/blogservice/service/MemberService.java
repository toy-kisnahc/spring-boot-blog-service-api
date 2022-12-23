package com.kisnahc.blogservice.service;

import com.kisnahc.blogservice.auth.provider.JwtProvider;
import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.domain.Role;
import com.kisnahc.blogservice.dto.reqeust.LoginMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.LogoutMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.UpdateMemberRequest;
import com.kisnahc.blogservice.dto.response.LoginMemberResponse;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.response.CreateMemberResponse;
import com.kisnahc.blogservice.dto.response.LogoutMemberResponse;
import com.kisnahc.blogservice.dto.response.UpdateMemberResponse;
import com.kisnahc.blogservice.exception.member.MemberNotFoundException;
import com.kisnahc.blogservice.repository.MemberRepository;
import com.kisnahc.blogservice.util.BlogValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlogValidator blogValidator;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public CreateMemberResponse save(CreateMemberRequest request) {
        blogValidator.duplicateValidation(request);

        Member member = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_ADMIN)
                .build();

        Member savedMember = memberRepository.save(member);

        return new CreateMemberResponse(savedMember);
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        Member member = blogValidator.LoginValidation(request);
        String jwt = jwtProvider.generateJwt(member.getEmail());
        return new LoginMemberResponse(member.getNickname(), jwt);
    }

    @Transactional
    public LogoutMemberResponse logout(LogoutMemberRequest request) {
        String email = jwtProvider.getEmail(request.getJwt());
        Duration expirationSeconds = getExpirationSeconds(request.getJwt());
        ValueOperations<String, String> logoutOperation = redisTemplate.opsForValue();
        logoutOperation.set(request.getJwt(), request.getJwt(), expirationSeconds);

        return new LogoutMemberResponse(email);
    }

    @Transactional
    public UpdateMemberResponse update(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        member.updateMember(request.getNickname());

        return new UpdateMemberResponse(member);
    }

    private Duration getExpirationSeconds(String jwt) {
        long expirationTime = jwtProvider.getExpiration(jwt).getTime();

        Date now = new Date();
        long expiration = expirationTime - now.getTime();

        return Duration.ofSeconds(expiration / 1000);
    }
}

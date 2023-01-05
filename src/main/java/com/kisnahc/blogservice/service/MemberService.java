package com.kisnahc.blogservice.service;

import com.kisnahc.blogservice.auth.provider.JwtProvider;
import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.domain.Role;
import com.kisnahc.blogservice.dto.reqeust.member.*;
import com.kisnahc.blogservice.dto.response.member.*;
import com.kisnahc.blogservice.dto.response.member.UpdateMemberResponse;
import com.kisnahc.blogservice.exception.member.MemberNotFoundException;
import com.kisnahc.blogservice.repository.member.MemberRepository;
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
import java.util.List;

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
                .role(Role.ROLE_MEMBER)
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

    @Transactional
    public DeleteMemberResponse delete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);

        return new DeleteMemberResponse(member);
    }

    public MemberResponse findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        return new MemberResponse(member);
    }

//    public List<MemberResponse> findMembers() {
//        List<Member> members = memberRepository.findAll();
//
//        return members.stream()
//                .map(MemberResponse::new)
//                .collect(Collectors.toList());
//    }

    public List<MemberResponse> findMembers(MemberSearchRequest memberSearchRequest) {
        return memberRepository.getMembers(memberSearchRequest);
    }
    private Duration getExpirationSeconds(String jwt) {
        long expirationTime = jwtProvider.getExpiration(jwt).getTime();

        Date now = new Date();
        long expiration = expirationTime - now.getTime();

        return Duration.ofSeconds(expiration / 1000);
    }
}

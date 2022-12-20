package com.kisnahc.blogservice.service;

import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.response.CreateMemberResponse;
import com.kisnahc.blogservice.repository.MemberRepository;
import com.kisnahc.blogservice.util.BlogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlogValidator blogValidator;

    @Transactional
    public CreateMemberResponse save(CreateMemberRequest request) {
        blogValidator.duplicateValidation(request);

        Member member = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Member savedMember = memberRepository.save(member);

        return new CreateMemberResponse(savedMember);
    }
}

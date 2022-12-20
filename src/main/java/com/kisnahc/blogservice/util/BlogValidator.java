package com.kisnahc.blogservice.util;

import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.exception.member.DuplicateEmailException;
import com.kisnahc.blogservice.exception.member.DuplicateNicknameException;
import com.kisnahc.blogservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogValidator {

    private final MemberRepository memberRepository;

    public void duplicateValidation(CreateMemberRequest request) {

        boolean isValidEmail = memberRepository.existsByEmail(request.getEmail());
        boolean isValidNickname = memberRepository.existsByNickname(request.getNickname());

        if (isValidEmail) {
            throw new DuplicateEmailException("email", request.getEmail());
        } else if (isValidNickname) {
            throw new DuplicateNicknameException("nickname", request.getNickname());
        }
    }
}

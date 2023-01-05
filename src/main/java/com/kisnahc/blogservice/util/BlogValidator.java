package com.kisnahc.blogservice.util;

import com.kisnahc.blogservice.domain.Comment;
import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.dto.reqeust.member.CreateMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.member.LoginMemberRequest;
import com.kisnahc.blogservice.exception.comment.CommentInvalidException;
import com.kisnahc.blogservice.exception.member.DuplicateEmailException;
import com.kisnahc.blogservice.exception.member.DuplicateNicknameException;
import com.kisnahc.blogservice.exception.member.LoginFailedException;
import com.kisnahc.blogservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogValidator {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void duplicateValidation(CreateMemberRequest request) {

        boolean isValidEmail = memberRepository.existsByEmail(request.getEmail());
        boolean isValidNickname = memberRepository.existsByNickname(request.getNickname());

        if (isValidEmail) {
            throw new DuplicateEmailException("email", request.getEmail());
        } else if (isValidNickname) {
            throw new DuplicateNicknameException("nickname", request.getNickname());
        }
    }

    public Member LoginValidation(LoginMemberRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(LoginFailedException::new);
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new LoginFailedException();
        }
        return member;
    }

    public void validateSamePost(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new CommentInvalidException();
        }
    }
}

package com.kisnahc.blogservice.service;

import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.dto.reqeust.member.CreateMemberRequest;
import com.kisnahc.blogservice.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void create_member_test_success() {
        CreateMemberRequest member = getMember("memberA@gmail.com", "memberA", "Member1234!");

        memberService.save(member);

        List<Member> members = memberRepository.findAll();

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getNickname()).isEqualTo(member.getNickname());
        assertThat(members.get(0).getEmail()).isEqualTo(member.getEmail());
        assertThat(passwordEncoder.matches(member.getPassword(), members.get(0).getPassword())).isTrue();
    }

    private CreateMemberRequest getMember(String email, String nickname, String password) {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setEmail(email);
        createMemberRequest.setNickname(nickname);
        createMemberRequest.setPassword(password);
        return createMemberRequest;
    }

}
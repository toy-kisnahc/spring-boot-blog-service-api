package com.kisnahc.blogservice.auth;

import com.kisnahc.blogservice.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberAdapter extends User {

    private final Member member;

    public MemberAdapter(Member member) {
        super(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority(member.getRole().toString())));
        this.member = member;
    }
}

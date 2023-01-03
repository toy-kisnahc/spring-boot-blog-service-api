package com.kisnahc.blogservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseTimeEntity{

    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String email, String nickname, String password, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void updateMember(String nickname) {
        this.nickname = nickname;
    }
}

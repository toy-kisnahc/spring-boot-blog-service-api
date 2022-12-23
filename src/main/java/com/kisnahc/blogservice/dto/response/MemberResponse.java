package com.kisnahc.blogservice.dto.response;

import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private Role role;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public MemberResponse(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = member.getRole();
        this.createdDate = member.getCreatedDate();
        this.updatedDate = member.getUpdatedDate();
    }
}

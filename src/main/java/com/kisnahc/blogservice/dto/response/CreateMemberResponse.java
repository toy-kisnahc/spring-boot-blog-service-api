package com.kisnahc.blogservice.dto.response;

import com.kisnahc.blogservice.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateMemberResponse {

    private Long memberId;
    private String email;
    private String nickname;

    public CreateMemberResponse(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}

package com.kisnahc.blogservice.dto.response;

import com.kisnahc.blogservice.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateMemberResponse {

    private Long memberId;
    private String updatedNickname;

    public UpdateMemberResponse(Member member) {
        this.memberId = member.getId();
        this.updatedNickname = member.getNickname();
    }
}

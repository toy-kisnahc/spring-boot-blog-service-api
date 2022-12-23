package com.kisnahc.blogservice.dto.response;

import com.kisnahc.blogservice.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DeleteMemberResponse {

    private Long deletedId;
    private String deletedMember;

    public DeleteMemberResponse(Member member) {
        this.deletedId = member.getId();
        this.deletedMember = member.getEmail();
    }
}

package com.kisnahc.blogservice.dto.response.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LogoutMemberResponse {

    private String logoutMember;

    public LogoutMemberResponse(String email) {
        this.logoutMember = email;
    }
}

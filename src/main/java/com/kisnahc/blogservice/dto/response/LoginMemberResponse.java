package com.kisnahc.blogservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginMemberResponse {

    private String loginMember;
    private String jwt;

    public LoginMemberResponse(String loginMember, String jwt) {
        this.loginMember = loginMember;
        this.jwt = jwt;
    }
}

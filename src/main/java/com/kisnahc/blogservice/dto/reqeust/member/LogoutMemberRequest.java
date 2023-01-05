package com.kisnahc.blogservice.dto.reqeust.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LogoutMemberRequest {

    private String jwt;
}

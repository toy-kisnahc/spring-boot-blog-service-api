package com.kisnahc.blogservice.dto.reqeust;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LogoutMemberRequest {

    private String jwt;
}

package com.kisnahc.blogservice.repository.member;

import com.kisnahc.blogservice.dto.reqeust.member.MemberSearchRequest;
import com.kisnahc.blogservice.dto.response.member.MemberResponse;

import java.util.List;


public interface MemberRepositoryCustom {
    List<MemberResponse> getMembers(MemberSearchRequest memberSearchRequest);
}

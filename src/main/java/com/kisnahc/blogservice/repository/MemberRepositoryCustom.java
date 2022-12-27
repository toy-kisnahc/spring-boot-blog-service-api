package com.kisnahc.blogservice.repository;

import com.kisnahc.blogservice.dto.reqeust.MemberSearchRequest;
import com.kisnahc.blogservice.dto.response.MemberResponse;

import java.util.List;


public interface MemberRepositoryCustom {
    List<MemberResponse> getMembers(MemberSearchRequest memberSearchRequest);
}

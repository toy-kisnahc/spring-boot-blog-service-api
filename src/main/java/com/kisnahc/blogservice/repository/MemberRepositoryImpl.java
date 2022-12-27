package com.kisnahc.blogservice.repository;

import com.kisnahc.blogservice.dto.reqeust.MemberSearchRequest;
import com.kisnahc.blogservice.dto.response.MemberResponse;
import com.kisnahc.blogservice.dto.response.QMemberResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kisnahc.blogservice.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MemberResponse> getMembers(MemberSearchRequest searchRequest) {

        return jpaQueryFactory
                .select(new QMemberResponse(member))
                .from(member)
                .offset(searchRequest.getOffset())
                .limit(searchRequest.getSize())
                .orderBy(member.id.desc())
                .fetch();
    }
}

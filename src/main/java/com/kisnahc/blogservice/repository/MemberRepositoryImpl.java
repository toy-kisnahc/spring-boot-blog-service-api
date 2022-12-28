package com.kisnahc.blogservice.repository;

import com.kisnahc.blogservice.dto.reqeust.MemberSearchRequest;
import com.kisnahc.blogservice.dto.response.MemberResponse;
import com.kisnahc.blogservice.dto.response.QMemberResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
                .orderBy(memberSort(searchRequest))
                .fetch();
    }

    private OrderSpecifier<?> memberSort(MemberSearchRequest memberSearchRequest) {
        if (!memberSearchRequest.getSort().isEmpty()) {
            for (Sort.Order order : memberSearchRequest.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "id" -> {
                        return new OrderSpecifier<>(direction, member.id);
                    }
                    case "nickname" -> {
                        return new OrderSpecifier<>(direction, member.nickname);
                    }
                    case "email" -> {
                        return new OrderSpecifier<>(direction, member.email);
                    }
                    case "createdData" -> {
                        return new OrderSpecifier<>(direction, member.createdDate);
                    }
                    case "updatedDate" -> {
                        return new OrderSpecifier<>(direction, member.updatedDate);
                    }
                }
            }
        }
        return null;
    }
}

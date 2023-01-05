package com.kisnahc.blogservice.dto.reqeust.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberSearchRequest {

    private static final int MAX_SIZE = 200;

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 20;

    private String sortBy;

    private Direction direction;

    private String nicknameContains;

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

    public Sort getSort() {
        return Sort.by(getDirection(), getSortBy());
    }
}

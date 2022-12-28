package com.kisnahc.blogservice.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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

    public long getOffset() {
        return (long) (Math.max(1, page) - 1) * Math.min(size, MAX_SIZE);
    }

    public Sort getSort() {
        return Sort.by(getDirection(), getSortBy());
    }
}

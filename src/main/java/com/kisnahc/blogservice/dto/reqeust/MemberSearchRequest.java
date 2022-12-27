package com.kisnahc.blogservice.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public long getOffset() {
        return (long) (Math.max(1, page) - 1) * Math.min(size, MAX_SIZE);
    }

}

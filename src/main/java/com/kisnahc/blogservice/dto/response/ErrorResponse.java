package com.kisnahc.blogservice.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class ErrorResponse {

    private String message;
    private int statusCode;
    private Map<String, String> errorField = new HashMap<>();

    @Builder
    public ErrorResponse(String message, int statusCode, Map<String, String> errorField) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorField = errorField;
    }

}

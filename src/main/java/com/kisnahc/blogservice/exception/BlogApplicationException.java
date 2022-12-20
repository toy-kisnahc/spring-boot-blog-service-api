package com.kisnahc.blogservice.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BlogApplicationException extends RuntimeException{

    private final Map<String, String> errorField = new HashMap<>();

    public BlogApplicationException(String message) {
        super(message);
    }

    public abstract int statusCode();

    public void addErrorField(String fieldName, String rejectValue) {
        errorField.put(fieldName, rejectValue);
    }
}

package com.kisnahc.blogservice.exception.member;

import com.kisnahc.blogservice.exception.BlogApplicationException;


public class MemberNotFoundException extends BlogApplicationException{

    private static final String MESSAGE = "member not found";

    public MemberNotFoundException(String fieldName, String rejectValue) {
        super(MESSAGE);
        addErrorField(fieldName, rejectValue);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}

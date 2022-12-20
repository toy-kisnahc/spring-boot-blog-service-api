package com.kisnahc.blogservice.exception.member;

import com.kisnahc.blogservice.exception.BlogApplicationException;

public class DuplicateNicknameException extends BlogApplicationException {

    private static final String MESSAGE = "This nickname is already in use.";

    public DuplicateNicknameException(String fieldName, String rejectValue) {
        super(MESSAGE);
        addErrorField(fieldName, rejectValue);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}

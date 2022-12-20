package com.kisnahc.blogservice.exception.member;

import com.kisnahc.blogservice.exception.BlogApplicationException;

public class DuplicateEmailException extends BlogApplicationException {

    private static final String MESSAGE = "This email is already in use.";

    public DuplicateEmailException(String field, String rejectValue) {
        super(MESSAGE);
        addErrorField(field, rejectValue);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}

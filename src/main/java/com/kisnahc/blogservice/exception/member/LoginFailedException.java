package com.kisnahc.blogservice.exception.member;

import com.kisnahc.blogservice.exception.BlogApplicationException;

public class LoginFailedException extends BlogApplicationException {

    private static final String MESSAGE = "Please check your email or password";
    public LoginFailedException() {
        super(MESSAGE);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}

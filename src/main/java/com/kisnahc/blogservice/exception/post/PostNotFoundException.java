package com.kisnahc.blogservice.exception.post;

import com.kisnahc.blogservice.exception.BlogApplicationException;

public class PostNotFoundException extends BlogApplicationException {

    private static final String MESSAGE = "Post not found";
    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}

package com.kisnahc.blogservice.exception.comment;

import com.kisnahc.blogservice.exception.BlogApplicationException;

public class CommentInvalidException extends BlogApplicationException {

    private static final String MESSAGE = "This is not a comment from the same post.";

    public CommentInvalidException() {
        super(MESSAGE);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}

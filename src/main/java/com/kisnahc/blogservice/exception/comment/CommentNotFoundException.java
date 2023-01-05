package com.kisnahc.blogservice.exception.comment;

import com.kisnahc.blogservice.exception.BlogApplicationException;

public class CommentNotFoundException extends BlogApplicationException {

    private static final String MESSAGE = "Comment not found";

    public CommentNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}

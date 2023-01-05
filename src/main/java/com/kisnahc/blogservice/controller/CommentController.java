package com.kisnahc.blogservice.controller;

import com.kisnahc.blogservice.auth.MemberAdapter;
import com.kisnahc.blogservice.dto.reqeust.comment.CreateCommentRequest;
import com.kisnahc.blogservice.dto.response.comment.CreateCommentResponse;
import com.kisnahc.blogservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<?> saveComment(@PathVariable Long postId,
                                         @RequestBody @Valid CreateCommentRequest request,
                                         @AuthenticationPrincipal MemberAdapter memberAdapter) {
        CreateCommentResponse commentResponse = commentService.save(postId, request, memberAdapter);
        return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);
    }
}

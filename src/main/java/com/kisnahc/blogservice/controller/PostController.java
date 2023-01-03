package com.kisnahc.blogservice.controller;

import com.kisnahc.blogservice.auth.MemberAdapter;
import com.kisnahc.blogservice.dto.reqeust.CreatePostRequest;
import com.kisnahc.blogservice.dto.response.CreatePostResponse;
import com.kisnahc.blogservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/api/posts")
    public ResponseEntity<CreatePostResponse> savePost(@RequestBody @Valid CreatePostRequest request,
                                   @AuthenticationPrincipal MemberAdapter memberAdapter) {

        CreatePostResponse createPostResponse = postService.save(request, memberAdapter);

        return new ResponseEntity<>(createPostResponse, HttpStatus.CREATED);
    }
}

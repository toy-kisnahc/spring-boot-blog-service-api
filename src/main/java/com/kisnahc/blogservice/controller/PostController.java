package com.kisnahc.blogservice.controller;

import com.kisnahc.blogservice.auth.MemberAdapter;
import com.kisnahc.blogservice.dto.reqeust.post.CreatePostRequest;
import com.kisnahc.blogservice.dto.response.post.CreatePostResponse;
import com.kisnahc.blogservice.dto.response.post.PostWithCommentResponse;
import com.kisnahc.blogservice.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<PostWithCommentResponse> findPost(@PathVariable Long postId,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        abusingViewCount(postId, request, response);
        PostWithCommentResponse postResponse = postService.getPostWithComment(postId);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    private void abusingViewCount(Long postId, HttpServletRequest request, HttpServletResponse response) {
        // 조회수 증복 증가 방지
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }
        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + postId.toString() + "]")) {
                postService.updateViewCount(postId);
                oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            postService.updateViewCount(postId);
            Cookie newCookie = new Cookie("postView", "[" + postId + "]");
            newCookie.setMaxAge(60 * 60 * 24);
            newCookie.setPath("/");
            response.addCookie(newCookie);
        }
    }
}

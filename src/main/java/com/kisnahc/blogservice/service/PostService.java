package com.kisnahc.blogservice.service;

import com.kisnahc.blogservice.auth.MemberAdapter;
import com.kisnahc.blogservice.domain.Post;
import com.kisnahc.blogservice.dto.reqeust.CreatePostRequest;
import com.kisnahc.blogservice.dto.response.CreatePostResponse;
import com.kisnahc.blogservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public CreatePostResponse save(CreatePostRequest request, MemberAdapter memberAdapter) {

        Post post = Post.builder()
                .author(memberAdapter.getMember())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post savedPost = postRepository.save(post);

        return new CreatePostResponse(savedPost, savedPost.getAuthor());
    }

}

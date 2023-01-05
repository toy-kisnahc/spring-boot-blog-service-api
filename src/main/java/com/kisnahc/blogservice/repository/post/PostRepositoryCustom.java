package com.kisnahc.blogservice.repository.post;

import com.kisnahc.blogservice.dto.response.post.PostWithCommentResponse;

import java.util.Optional;

public interface PostRepositoryCustom {

    Optional<PostWithCommentResponse> findOneById(Long postId);
}

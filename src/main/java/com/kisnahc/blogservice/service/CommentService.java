package com.kisnahc.blogservice.service;

import com.kisnahc.blogservice.auth.MemberAdapter;
import com.kisnahc.blogservice.domain.Comment;
import com.kisnahc.blogservice.domain.Post;
import com.kisnahc.blogservice.dto.reqeust.comment.CreateCommentRequest;
import com.kisnahc.blogservice.dto.response.comment.CreateCommentResponse;
import com.kisnahc.blogservice.exception.comment.CommentNotFoundException;
import com.kisnahc.blogservice.exception.post.PostNotFoundException;
import com.kisnahc.blogservice.repository.comment.CommentRepository;
import com.kisnahc.blogservice.repository.post.PostRepository;
import com.kisnahc.blogservice.util.BlogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BlogValidator blogValidator;

    @Transactional
    public CreateCommentResponse save(Long postId, CreateCommentRequest request, MemberAdapter memberAdapter) {

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        Comment comment = Comment.builder()
                .post(post)
                .author(memberAdapter.getMember())
                .content(request.getContent())
                .parent(request.getParentId() != null ?
                        commentRepository.findById(request.getParentId())
                                .orElseThrow(CommentNotFoundException::new) : null)
                .build();

        if (comment.getParent() != null) {
            blogValidator.validateSamePost(postId, comment);
        }

        Comment savedComment = commentRepository.save(comment);
        return new CreateCommentResponse(savedComment);
    }

}

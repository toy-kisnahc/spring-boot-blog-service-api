package com.kisnahc.blogservice.dto.response.post;

import com.kisnahc.blogservice.domain.Post;
import com.kisnahc.blogservice.dto.response.comment.CommentResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PostWithCommentResponse {

    private Long postId;
    private String title;
    private String content;
    private String author;
    private int view;
    private List<CommentResponse> comments;

    @QueryProjection
    public PostWithCommentResponse(Post post) {
        this.postId = post.getId();
        this.author = post.getAuthor().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.view = post.getView();
    }
}

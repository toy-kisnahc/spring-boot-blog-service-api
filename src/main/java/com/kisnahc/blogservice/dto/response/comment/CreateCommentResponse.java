package com.kisnahc.blogservice.dto.response.comment;

import com.kisnahc.blogservice.domain.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateCommentResponse {

    private Long commentId;
    private Long postId;
    private String author;
    private String content;

    public CreateCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.author = comment.getAuthor().getNickname();
        this.content = comment.getContent();
    }
}

package com.kisnahc.blogservice.dto.response.comment;

import com.kisnahc.blogservice.domain.Comment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@Data
public class CommentResponse {

    private Long commentId;
    private Comment parent;
    private String author;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<ChildCommentResponse> children;

    @QueryProjection
    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.parent = comment.getParent();
        this.author = comment.getAuthor().getNickname();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedDate();
        this.updatedDate = comment.getUpdatedDate();
    }

    public void setChildren(List<ChildCommentResponse> commentResponses) {
        this.children = commentResponses;
    }
}

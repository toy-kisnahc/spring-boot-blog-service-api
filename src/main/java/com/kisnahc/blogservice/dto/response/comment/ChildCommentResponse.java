package com.kisnahc.blogservice.dto.response.comment;

import com.kisnahc.blogservice.domain.Comment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ChildCommentResponse {

    private Long commentId;
    private Long parentId;
    private String author;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @QueryProjection
    public ChildCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.parentId = comment.getParent().getId();
        this.author = comment.getAuthor().getNickname();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedDate();
        this.updatedDate = comment.getUpdatedDate();
    }
}

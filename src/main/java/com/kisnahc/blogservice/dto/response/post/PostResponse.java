package com.kisnahc.blogservice.dto.response.post;

import com.kisnahc.blogservice.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private String author;
    private int view;
    private int commentCount;

    public PostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor().getNickname();
        this.view = post.getView();
        this.commentCount = post.getComments().size();
    }
}

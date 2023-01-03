package com.kisnahc.blogservice.dto.response;

import com.kisnahc.blogservice.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PostResponse {

    private String title;
    private String content;
    private String author;
    private int view;

    public PostResponse(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor().getNickname();
        this.view = post.getView();
    }
}

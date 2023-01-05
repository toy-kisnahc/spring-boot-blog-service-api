package com.kisnahc.blogservice.dto.response.post;

import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreatePostResponse {

    private String author;
    private String title;
    private String content;
    private int view;

    public CreatePostResponse(Post post, Member member) {
        this.author = member.getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.view = post.getView();
    }
}

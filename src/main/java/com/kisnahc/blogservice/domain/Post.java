package com.kisnahc.blogservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int view;

    @Builder
    public Post(Member author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

}

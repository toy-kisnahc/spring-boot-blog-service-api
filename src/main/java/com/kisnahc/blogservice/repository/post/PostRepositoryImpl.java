package com.kisnahc.blogservice.repository.post;

import com.kisnahc.blogservice.dto.response.comment.ChildCommentResponse;
import com.kisnahc.blogservice.dto.response.comment.CommentResponse;
import com.kisnahc.blogservice.dto.response.comment.QChildCommentResponse;
import com.kisnahc.blogservice.dto.response.comment.QCommentResponse;
import com.kisnahc.blogservice.dto.response.post.PostWithCommentResponse;
import com.kisnahc.blogservice.dto.response.post.QPostWithCommentResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


import static com.kisnahc.blogservice.domain.QComment.comment;
import static com.kisnahc.blogservice.domain.QPost.post;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<PostWithCommentResponse> findOneById(Long postId) {
        Optional<PostWithCommentResponse> response = Optional.ofNullable(jpaQueryFactory
                .select(new QPostWithCommentResponse(post))
                .from(post)
                .where(post.id.eq(postId))
                .fetchOne());

        if (response.isEmpty()) {
            return Optional.empty();
        }

        List<CommentResponse> comments = jpaQueryFactory
                .select(new QCommentResponse(comment))
                .from(comment)
                .where(post.id.eq(postId).and(comment.parent.id.isNull()))
                .orderBy(comment.id.desc())
                .fetch();

        List<ChildCommentResponse> childComments = jpaQueryFactory
                .select(new QChildCommentResponse(comment))
                .from(comment)
                .where(post.id.eq(postId).and(comment.parent.id.isNotNull()))
                .orderBy(comment.id.desc())
                .fetch();

        for (CommentResponse parent : comments) {
            List<ChildCommentResponse> list = new ArrayList<>();
            for (ChildCommentResponse child : childComments) {
                if (Objects.equals(parent.getCommentId(), child.getParentId())) {
                    list.add(child);
                }
            }
            parent.setChildren(list);
        }

        response.get().setComments(comments);
        return response;
    }
}

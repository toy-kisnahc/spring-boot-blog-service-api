package com.kisnahc.blogservice.repository.comment;

import com.kisnahc.blogservice.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}

package com.study.domain.mapper.comment;


import com.study.domain.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentQueryMapper {

    Optional<Comment> findCommentById(long id);

    List<Comment> getCommentsByPostId(long postId);

    long getPostIdByCommentId(long commentId);
}

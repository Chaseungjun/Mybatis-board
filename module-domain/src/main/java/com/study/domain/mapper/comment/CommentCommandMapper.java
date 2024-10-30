package com.study.domain.mapper.comment;

import com.study.domain.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentCommandMapper {

    void save(@Param("comment") Comment comment);

    void update(@Param("content") String content, @Param("id") long id);

    void delete(@Param("id") long id);

    void deleteByPostId(@Param("postId") long postId);
}

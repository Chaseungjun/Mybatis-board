package com.study.domain.mapper.post;

import com.study.domain.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostQueryMapper {

    Optional<Post> findPostById(long id);

    List<String> findFileUrlsByPostId(long postId);

    List<Post> findAll();

    int count();

}

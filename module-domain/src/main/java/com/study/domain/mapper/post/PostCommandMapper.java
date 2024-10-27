package com.study.domain.mapper.post;


import com.study.domain.post.dto.PostDto;
import com.study.domain.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostCommandMapper {

    long register(Post post);

    void registerImages(PostDto.RegisterFileDto registerFileDto);

    void update(
            @Param("title") String title,
            @Param("content") String content,
            @Param("postId") long postId
    );

    void updateFileUrls(@Param("fileUrls") List<String> fileUrls, @Param("postId") long postId);

    void deleteFilesByPostId(long postId);

    void deleteByPostId(Post post);

}

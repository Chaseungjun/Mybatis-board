package com.study.domain.mapper.postTag;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostTagCommandMapper {

    void registerPostTags(@Param("postId") long postId, @Param("tagIds") List<Integer> tagIds);
    void deletePostTagsByPostId(long postId);  // 게시글 수정 시 기존 태그 삭제 용도

}
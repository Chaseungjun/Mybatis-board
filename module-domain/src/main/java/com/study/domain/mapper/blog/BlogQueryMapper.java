package com.study.domain.mapper.blog;


import com.study.domain.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogQueryMapper {
    Blog findBlogByUserId(String userId);

    Blog findBlogByBlogId(long blogId);
}

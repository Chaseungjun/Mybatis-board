package com.study.domain.mapper.blog;


import com.study.domain.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogCommandMapper {

    void save(Blog blog);

    void withdraw(@Param("blog") Blog blog);
}

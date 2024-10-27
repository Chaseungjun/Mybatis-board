package com.study.domain.mapper.bloguser;


import com.study.domain.blogUser.entity.BlogUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogUserCommandMapper {

    long save(@Param("blogUser") BlogUser blogUser);

    void withdraw(@Param("blogUser") BlogUser blogUser);

}

package com.study.domain.mapper.liked;

import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikedQueryMapper {

    boolean existsByUserAndPost(@Param("user") BlogUser user, @Param("post") Post post);

}

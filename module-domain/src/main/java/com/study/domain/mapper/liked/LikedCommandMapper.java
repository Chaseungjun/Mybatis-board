package com.study.domain.mapper.liked;

import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.liked.entity.Liked;
import com.study.domain.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikedCommandMapper {

    void save(Liked liked);

    void deleteLikesByUserAndPost(@Param("user") BlogUser user, @Param("post") Post post);


}

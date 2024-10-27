package com.study.api.permission.service;


import com.study.common.exception.user.NotFoundUserException;
import com.study.domain.blog.entity.Blog;

import com.study.domain.blogUser.entity.BlogUser;

import com.study.domain.mapper.blog.BlogQueryMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.permission.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final BlogQueryMapper blogQueryMapper;
    private final BlogUserQueryMapper blogUserQueryMapper;


    public UserRole getUserRoleForBlog(long blogId, String userId) {
        Blog blog = blogQueryMapper.findBlogByBlogId(blogId);
        BlogUser user = blogUserQueryMapper.findBlogUserByUserId(userId)
                .orElseThrow(() -> new NotFoundUserException(userId));

        return determineUserRole(blog, user);
    }

    /**
     * 주어진 사용자와 블로그에 대한 권한을 결정합니다.
     *
     * @param blog 블로그
     * @param user 사용자
     * @return 권한
     */
    public UserRole determineUserRole(Blog blog, BlogUser user) {
        if (blog.getUserId().equals(user.getUserId())) {
            return UserRole.ROLE_ADMIN;
        } else if (!blog.getUserId().equals(user.getUserId())) {
            return UserRole.ROLE_MEMBER;
        } else {
            return UserRole.ROLE_GUEST;
        }
    }

}

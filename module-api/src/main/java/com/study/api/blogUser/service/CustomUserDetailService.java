package com.study.api.blogUser.service;

import com.study.api.permission.service.PermissionService;
import com.study.common.exception.user.NotFoundUserException;
import com.study.domain.blog.entity.Blog;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.mapper.blog.BlogQueryMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.permission.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static org.springframework.security.core.userdetails.User.builder;

/**
 * 사용자 세부 정보를 로드하는 서비스를 구현한 클래스입니다. 사용자가 인증을 요청할 때 사용됩니다.
 */
@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final BlogQueryMapper blogQueryMapper;
    private final BlogUserQueryMapper userQueryMapper;
    private final PermissionService permissionService;

    /**
     * 사용자 ID를 통해 사용자의 세부 정보를 로드합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 세부 정보
     * @throws UsernameNotFoundException 사용자 ID에 해당하는 사용자가 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        BlogUser user = userQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));
        Blog blog = blogQueryMapper.findBlogByUserId(user.getUserId());
        UserRole userRole = permissionService.getUserRoleForBlog(blog.getId(), userId);

        return builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(userRole.name())))
                .build();
    }
}
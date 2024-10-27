package com.study.api.blogUser.service;

import com.study.common.exception.user.NotFoundUserException;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.mapper.bloguser.BlogUserCommandMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogUserWithdrawService {

    private final BlogUserQueryMapper blogUserQueryMapper;
    private final BlogUserCommandMapper blogUserCommandMapper;

    @Transactional
    public void withdrawBlogUser(String userId) {
        BlogUser blogUser = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));
        blogUserCommandMapper.withdraw(blogUser);
    }
}

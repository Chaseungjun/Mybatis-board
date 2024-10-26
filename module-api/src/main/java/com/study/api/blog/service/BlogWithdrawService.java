package com.study.api.blog.service;

import com.study.domain.blog.entity.Blog;
import com.study.domain.mapper.blog.BlogCommandMapper;
import com.study.domain.mapper.blog.BlogQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogWithdrawService {

    private final BlogCommandMapper blogCommandMapper;
    private final BlogQueryMapper blogQueryMapper;

    @Transactional
    public void withdraw(String userId){
        Blog blog = blogQueryMapper.findBlogByUserId(userId);
        blogCommandMapper.withdraw(blog);
    }
}

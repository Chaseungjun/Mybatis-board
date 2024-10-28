package com.study.api.liked.service;

import com.study.common.exception.ErrorCode;
import com.study.common.exception.liked.PostDeleteLikeException;
import com.study.common.exception.liked.PostLikedException;
import com.study.common.exception.post.NotExistPostException;
import com.study.common.exception.user.NotFoundUserException;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.liked.entity.Liked;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.mapper.liked.LikedCommandMapper;
import com.study.domain.mapper.liked.LikedQueryMapper;
import com.study.domain.mapper.post.PostCommandMapper;
import com.study.domain.mapper.post.PostQueryMapper;
import com.study.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikedService {

    private final LikedQueryMapper likedQueryMapper;
    private final LikedCommandMapper likedCommandMapper;
    private final PostQueryMapper postQueryMapper;
    private final PostCommandMapper postCommandMapper;
    private final BlogUserQueryMapper blogUserQueryMapper;

    @Transactional
    public void addLike(int postId, String userId) {

        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        BlogUser blogUser = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));

        if (!checkLikeExist(blogUser, post)) {
            likedCommandMapper.save(new Liked(blogUser.getUserId(), post.getId()));
            post.plusLikedCount();
            postCommandMapper.addLike(postId);
        } else {
            throw new PostLikedException(ErrorCode.POST_LIKE_FAIL_EXCEPTION);
        }
    }

    @Transactional
    public void minusLike(int postId, String userId) {
        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        BlogUser blogUser = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));

        if (checkLikeExist(blogUser, post)) {
            likedCommandMapper.deleteLikesByUserAndPost(blogUser, post);
            post.minusLikedCount();
            postCommandMapper.minusLike(postId);
        } else {
            throw new PostDeleteLikeException(ErrorCode.POST_DELETE_LIKE_FAIL_EXCEPTION);
        }
    }

    private boolean checkLikeExist(BlogUser user, Post post) {
        return likedQueryMapper.existsByUserAndPost(user, post);
    }
}


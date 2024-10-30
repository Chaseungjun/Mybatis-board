package com.study.domain.redis.popularPost.service;

import com.study.domain.comment.entity.Comment;
import com.study.domain.mapper.comment.CommentQueryMapper;
import com.study.domain.mapper.post.PostQueryMapper;
import com.study.domain.post.dto.PostDto;
import com.study.domain.post.entity.Post;
import com.study.domain.redis.popularPost.entity.PopularPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PopularPostCacheService {


    private final RedisTemplate<String, Object> redisTemplate;
    private final PostQueryMapper postQueryMapper;
    private final CommentQueryMapper commentQueryMapper;

    private final String POPULAR_POST_KEY = "popularPost:";

    public void cachePopularPost(Post post) {
        String key = POPULAR_POST_KEY + post.getId();

        List<String> fileUrls = postQueryMapper.findFileUrlsByPostId(post.getId());
        List<Comment> comments = commentQueryMapper.getCommentsByPostId(post.getId());

        // PopularPost 객체 생성
        PopularPost popularPost = createPopularPost(post, fileUrls, comments);

        PostDto.PostResponse postResponse = PostDto.PostResponse.fromPopularPost(popularPost);
        redisTemplate.opsForValue().set(key, postResponse);
    }


    public PostDto.PostResponse getCachedPopularPost(long postId) {
        return (PostDto.PostResponse) redisTemplate.opsForValue().get(POPULAR_POST_KEY + postId);
    }

    public void removePopularPostCache(long postId) {
        redisTemplate.delete(POPULAR_POST_KEY + postId);
    }

    private static PopularPost createPopularPost(Post post, List<String> fileUrls, List<Comment> comments) {
        PopularPost popularPost = PopularPost.builder()
                .postId(post.getId())
                .blogId(post.getBlogId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickName(post.getNickName())
                .fileUrls(fileUrls)
                .comments(comments)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .build();
        return popularPost;
    }
}
package com.study.domain.comment.entity;

import com.study.domain.blog.entity.Blog;
import com.study.domain.comment.dto.CommentDto;
import com.study.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {


    /**
     * 댓글 식별자 id
     */
    private long id;

    /**
     * 댓글이 등록된 블로그 id
     */
    private long blogId;

    /**
     * 댓글을 작성한 유저ID
     */
    private String userId;

    /**
     * 댓글이 등록된 게시글 id
     */
    private long postId;

    /**
     * 댓글 내용
     */
    private String content;

    @Builder
    public Comment(long blogId, String userId, String content, long postId) {
        this.blogId = blogId;
        this.userId = userId;
        this.content = content;
        this.postId = postId;
    }

    public static Comment of(Blog blog, String userId, Post post, CommentDto.CommentRegisterDto registerDto) {
        return Comment.builder()
                .blogId(blog.getId())
                .userId(userId)
                .postId(post.getId())
                .content(registerDto.content())
                .build();
    }

}

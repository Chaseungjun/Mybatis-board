package com.study.domain.redis.popularPost.entity;

import com.study.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RedisHash(value = "PopularPost", timeToLive = 86400)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularPost {

    @Id
    private long postId;
    private long blogId;
    private String userId;
    private String title;
    private String content;
    private String nickName;
    private List<String> fileUrls;
    private List<Comment> comments;
    private int likeCount;
    private int commentCount;
    private boolean isPopular;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime deletedDate;


    @Builder
    public PopularPost(
            long postId,
            long blogId,
            String userId,
            String title,
            String content,
            String nickName,
            List<String> fileUrls,
            List<Comment> comments,
            int likeCount,
            int commentCount,
            boolean isPopular,
            LocalDateTime createdDate,
            LocalDateTime modifiedDate,
            LocalDateTime deletedDate
    ) {
        this.postId = postId;
        this.blogId = blogId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.nickName = nickName;
        this.fileUrls = fileUrls;
        this.comments = comments;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isPopular = isPopular;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = null;
        this.deletedDate = null;
    }
}
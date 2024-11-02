package com.study.domain.post.entity;

import com.study.domain.comment.entity.Comment;
import com.study.domain.post.dto.PostDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글을 나타내는 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {


    private long id;
    private long blogId;
    private String userId;
    private String title;
    private String content;
    private String nickName;
    private List<String> fileUrls;
    private List<Comment> comments = new ArrayList<>();
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private boolean isPopular;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime deletedDate;

    @Builder
    public Post(
            long blogId,
            String userId,
            String nickName,
            String title,
            String content,
            List<String> fileUrls,
            List<Comment> comments,
            int likeCount,
            boolean isPopular,
            int commentCount,
            int viewCount,
            LocalDateTime createdDate,
            LocalDateTime modifiedDate,
            LocalDateTime deletedDate
    ) {
        this.blogId = blogId;
        this.userId = userId;
        this.nickName = nickName;
        this.title = title;
        this.content = content;
        this.fileUrls = fileUrls;
        this.comments = comments;
        this.likeCount = likeCount;
        this.isPopular = isPopular;
        this.commentCount = commentCount;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = null;
        this.deletedDate = null;
    }

    public static Post of(PostDto.PostRegisterDto postRegisterDto, long blogId, String userId, String nickName, List<String> fileUrls) {
        return Post.builder()
                .blogId(blogId)
                .userId(userId)
                .nickName(nickName)
                .title(postRegisterDto.title())
                .content(postRegisterDto.content())
                .fileUrls(fileUrls)
                .createdDate(LocalDateTime.now())
                .isPopular(false)
                .commentCount(0)
                .viewCount(0)
                .likeCount(0)
                .build();
    }

    public int getCommentCount() {
        return comments.size();
    }

    public void plusLikedCount() {
        likeCount = likeCount + 1;
    }

    public void minusLikedCount() {
        likeCount = likeCount - 1;
    }

    public void plusVieCount(){
        viewCount = viewCount + 1;
    }
}
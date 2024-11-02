package com.study.domain.post.dto;

import com.study.domain.comment.dto.CommentDto;
import com.study.domain.post.entity.Post;
import com.study.domain.redis.popularPost.entity.PopularPost;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Post에 대한 데이터 전송 객체(DTO)입니다.
 */
public class PostDto {

    /**
     * @param title   게시글 제목
     * @param content 게시글 내용
     */
    public record PostRegisterDto(
            @NotBlank(message = "제목을 입력해주세요") String title,
            @NotBlank(message = "내용을 입력해주세요") String content
    ) {
    }

    /**
     * @param postId   게시글의 고유식별자 id
     * @param fileUrls 게시글에 등록한 이미지 url 리스트
     */
    public record RegisterFileDto(
            long postId,
            List<String> fileUrls
    ) {
    }

    /**
     * @param title    게시글 제목
     * @param content  게시글 내용
     * @param fileUrls 게시글에 등록한 이미지 url 리스트
     */
    public record UpdatePostDto(
            long postId,
            @NotBlank(message = "제목을 입력해주세요") String title,
            @NotBlank(message = "내용을 입력해주세요") String content,
            List<String> fileUrls
    ) {
    }

    /**
     * @param postId  게시글 id
     * @param title   게시글 제목
     * @param content 게시글 내용
     * @param userId  게시글을 등록한 유저의 ID
     */
    @Builder
    public record PostResponse(
            long postId,
            String title,
            String content,
            String userId,
            List<String> fileUrls,
            List<CommentDto.CommentResponseDto> commentResponseDtos,
            int likeCount,
            int commentCount,
            int viewCount,
            boolean isPopular
    ) {

        public static PostResponse fromPost(Post post, List<CommentDto.CommentResponseDto> commentResponseDtos, List<String> fileUrls) {
            return PostResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .fileUrls(fileUrls)
                    .userId(post.getUserId())
                    .commentResponseDtos(commentResponseDtos)
                    .likeCount(post.getLikeCount())
                    .viewCount(post.getViewCount())
                    .isPopular(false)
                    .build();
        }

        public static PostResponse fromPopularPost(PopularPost popularPost) {
            List<CommentDto.CommentResponseDto> commentResponseDtoList = popularPost.getComments().stream().map(
                    CommentDto.CommentResponseDto::of
            ).collect(Collectors.toList());

            return PostResponse.builder()
                    .postId(popularPost.getPostId())
                    .title(popularPost.getTitle())
                    .content(popularPost.getContent())
                    .fileUrls(popularPost.getFileUrls())
                    .userId(popularPost.getUserId())
                    .commentResponseDtos(commentResponseDtoList)
                    .likeCount(popularPost.getLikeCount())
                    .commentCount(popularPost.getCommentCount())
                    .viewCount(popularPost.getViewCount())
                    .isPopular(true)
                    .likeCount(popularPost.getLikeCount())
                    .commentCount(popularPost.getCommentCount())
                    .build();
        }
    }

    @Builder
    public record PostListDto(
            long postId,
            String title,
            String userId,
            String nickName,
            int likeCount,
            int commentCount,
            int viewCount,
            LocalDateTime createdDate
    ) {
        public static PostListDto of(Post post) {
            return PostListDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .userId(post.getUserId())
                    .nickName(post.getNickName())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .viewCount(post.getViewCount())
                    .build();
        }
    }
}
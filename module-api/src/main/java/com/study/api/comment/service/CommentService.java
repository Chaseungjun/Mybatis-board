package com.study.api.comment.service;

import com.study.api.notification.service.NotificationService;
import com.study.common.exception.authentication.NotAuthenticationException;
import com.study.common.exception.comment.NotExistCommentException;
import com.study.common.exception.post.NotExistPostException;
import com.study.domain.blog.entity.Blog;
import com.study.domain.comment.dto.CommentDto;
import com.study.domain.comment.entity.Comment;
import com.study.domain.mapper.blog.BlogQueryMapper;
import com.study.domain.mapper.comment.CommentCommandMapper;
import com.study.domain.mapper.comment.CommentQueryMapper;
import com.study.domain.mapper.post.PostCommandMapper;
import com.study.domain.mapper.post.PostQueryMapper;
import com.study.domain.notification.entity.Notification;
import com.study.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글 관련 기능을 처리하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentCommandMapper commentCommandMapper;
    private final CommentQueryMapper commentQueryMapper;
    private final PostQueryMapper postQueryMapper;
    private final PostCommandMapper postCommandMapper;
    private final BlogQueryMapper blogQueryMapper;
    private final NotificationService notificationService;

    private final String URL = "/post/";


    /**
     * 댓글을 저장합니다.
     *
     * @param registerDto 댓글 등록 정보
     * @param userId 댓글 작성자 ID
     * @param blogId 블로그 ID
     * @param postId 게시물 ID
     * @return 저장된 댓글의 응답 정보
     */
    @Transactional
    public CommentDto.CommentResponseDto save
            (
                    CommentDto.CommentRegisterDto registerDto,
                    String userId,
                    long blogId,
                    long postId
            ) {

        Blog blog = blogQueryMapper.findBlogByBlogId(blogId);
        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);

        Comment comment = Comment.of(blog, userId, post, registerDto);
        validateWriter(comment, userId);

        commentCommandMapper.save(comment);
        postCommandMapper.addCommentCount(postId);

        String postUrl = URL + postId;
        notificationService.send(comment.getUserId(), Notification.NotificationType.COMMENT, comment.getContent(), postUrl);

        CommentDto.CommentResponseDto commentResponseDto = CommentDto.CommentResponseDto.of(comment);

        return commentResponseDto;
    }

    /**
     * 댓글을 수정합니다.
     *
     * @param commentId 수정할 댓글 ID
     * @param updateDto 수정 정보
     * @param userId 수정 요청 사용자 ID
     */
    @Transactional
    public void update(long commentId, CommentDto.CommentUpdateDto updateDto, String userId) {
        Comment comment = commentQueryMapper.findCommentById(commentId).orElseThrow(NotExistCommentException::new);
        validateWriter(comment, userId);
        commentCommandMapper.update(updateDto.content(), comment.getId());
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글 ID
     * @param userId 삭제 요청 사용자 ID
     */
    @Transactional
    public void delete(long commentId, String userId) {
        Comment comment = commentQueryMapper.findCommentById(commentId).orElseThrow(NotExistCommentException::new);
        long postId = commentQueryMapper.getPostIdByCommentId(commentId);
        validateWriter(comment, userId);
        commentCommandMapper.delete(comment.getId());
        postCommandMapper.minusCommentCount(postId);
    }

    /**
     * 댓글 작성자를 검증합니다.
     *
     * @param comment 댓글 정보
     * @param userId 검증할 사용자 ID
     */
    private void validateWriter(Comment comment, String userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new NotAuthenticationException(userId);
        }
    }
}

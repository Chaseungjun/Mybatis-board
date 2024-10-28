package com.study.api.comment.service;

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
import com.study.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentCommandMapper commentCommandMapper;
    private final CommentQueryMapper commentQueryMapper;
    private final PostQueryMapper postQueryMapper;
    private final PostCommandMapper postCommandMapper;
    private final BlogQueryMapper blogQueryMapper;

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

        CommentDto.CommentResponseDto commentResponseDto = CommentDto.CommentResponseDto.of(comment);

        return commentResponseDto;
    }

    @Transactional
    public void update(long commentId, CommentDto.CommentUpdateDto updateDto, String userId) {
        Comment comment = commentQueryMapper.findCommentById(commentId).orElseThrow(NotExistCommentException::new);
        validateWriter(comment, userId);
        commentCommandMapper.update(updateDto.content(), comment.getId());
    }

    @Transactional
    public void delete(long commentId, String userId) {
        Comment comment = commentQueryMapper.findCommentById(commentId).orElseThrow(NotExistCommentException::new);
        long postId = commentQueryMapper.getPostIdByCommentId(commentId);
        validateWriter(comment, userId);
        commentCommandMapper.delete(comment.getId());
        postCommandMapper.minusCommentCount(postId);
    }

    private void validateWriter(Comment comment, String userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new NotAuthenticationException(userId);
        }
    }
}

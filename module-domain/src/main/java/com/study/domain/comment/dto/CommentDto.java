package com.study.domain.comment.dto;

import com.study.domain.comment.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


public class CommentDto {

    public record CommentRegisterDto(
            @NotBlank(message = "내용을 입력해주세요") String content
    ) {}

    public record CommentUpdateDto(
            @NotBlank(message = "내용을 입력해주세요") String content
    ){}

    @Builder
    public record CommentResponseDto(
            String content,
            String userId
    ) {
        public static CommentResponseDto of(Comment comment) {
            return CommentResponseDto.builder()
                    .content(comment.getContent())
                    .userId(comment.getUserId())
                    .build();
        }
    }
}
package com.study.api.comment.controller;


import com.study.api.comment.service.CommentService;
import com.study.api.config.annotation.AuthenticatedUserId;
import com.study.domain.comment.dto.CommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록 API
     *
     * @param registerDto 댓글 등록 데이터
     * @return 등록된 댓글 정보
     */
    @Operation(
            method = "POST",
            summary = "댓글 등록",
            description = "내용을 입력받아 댓글을 작성한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.CommentRegisterDto.class))),
            @ApiResponse(responseCode = "400", description = "댓글 등록에 실패했습니다", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEMBER')")
    @PostMapping("{blogId}/{postId}")
    public ResponseEntity<CommentDto.CommentResponseDto> register
    (
            @Valid @RequestBody CommentDto.CommentRegisterDto registerDto,
            @AuthenticatedUserId String userId,
            @PathVariable long blogId,
            @PathVariable long postId
    ) {
        CommentDto.CommentResponseDto responseDto = commentService.save(registerDto, userId, blogId, postId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 댓글 수정 API
     *
     * @param updateDto 댓글 수정 데이터
     * @param userId    수정 요청을 보낸 유저 ID
     * @return HTTP 상태 코드
     */
    @Operation(
            method = "POST",
            summary = "댓글 수정",
            description = "댓글을 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.CommentUpdateDto.class))),
            @ApiResponse(responseCode = "400", description = "댓글 수정에 실패했습니다", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEMBER')")
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(
            @PathVariable long commentId,
            @Valid @RequestBody CommentDto.CommentUpdateDto updateDto,
            @AuthenticatedUserId String userId
    ) {
        commentService.update(commentId, updateDto, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 삭제 API
     *
     * @param commentId 삭제할 댓글 ID
     * @param userId    삭제 요청을 보낸 유저 ID
     * @return HTTP 상태 코드
     */
    @Operation(
            method = "Patch",
            summary = "댓글 삭제",
            description = "댓글을 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "댓글 삭제에 실패했습니다", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEMBER')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable long commentId, @AuthenticatedUserId String userId) {
        commentService.delete(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
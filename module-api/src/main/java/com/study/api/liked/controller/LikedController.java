package com.study.api.liked.controller;

import com.study.api.liked.service.LikedService;
import com.study.api.config.annotation.AuthenticatedUserId;
import com.study.domain.mapper.post.PostCommandMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 좋아요 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/liked")
@RequiredArgsConstructor
public class LikedController {

    private final LikedService likedService;
    private final PostCommandMapper postCommandMapper;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "좋아요 추가에 실패했습니다")
    })
    @Operation(
            method = "POST",
            summary = "좋아요 추가",
            description = "좋아요를 추가한다."
    )
    @PostMapping("/addLike")
    public ResponseEntity<Void> addLike(@RequestParam int postId, @AuthenticatedUserId String userId) {
        likedService.addLike(postId, userId);
        return ResponseEntity.ok().build();
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "좋아요 취소에 실패했습니다")
    })
    @Operation(
            method = "DELETE",
            summary = "좋아요 취소",
            description = "좋아요를 취소한다."
    )
    @DeleteMapping("/deleteLike")
    public ResponseEntity<Void> deleteLike(@RequestParam int postId, @AuthenticatedUserId String userId) {
        likedService.minusLike(postId, userId);
        return ResponseEntity.ok().build();
    }
}

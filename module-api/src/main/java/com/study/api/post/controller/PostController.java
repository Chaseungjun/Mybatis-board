package com.study.api.post.controller;


import com.study.api.post.service.PostService;
import com.study.api.config.annotation.AuthenticatedUserId;
import com.study.domain.post.dto.PagingResponse;
import com.study.domain.post.dto.PostDto;
import com.study.domain.post.dto.SearchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(
            method = "POST",
            summary = "게시글 등록",
            description = "제목과 내용을 입력받아 게시글을 작성한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "게시글 등록에 실패했습니다", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/register" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto.PostResponse> register(
            @RequestPart @Valid PostDto.PostRegisterDto postRegisterDto,
            @RequestPart(required = false) List<MultipartFile> files,
            @RequestPart(required = false) List<String> tags,
            @AuthenticatedUserId String userId
    ){
        return ResponseEntity.ok(postService.register(postRegisterDto, files, tags, userId));
    }

    @Operation(
            method = "POST",
            summary = "게시글 수정",
            description = "게시글을 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.UpdatePostDto.class))),
            @ApiResponse(responseCode = "400", description = "게시글 수정에 실패했습니다", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/update/{postId}")
    public ResponseEntity<PostDto.PostResponse> update(
            @RequestPart @Valid PostDto.UpdatePostDto updatePostDto,
            @RequestPart(required = false) List<MultipartFile> files,
            @RequestPart(required = false) List<String> tags,
            @AuthenticatedUserId String userId,
            @PathVariable Long postId
    ){
        PostDto.PostResponse updatedPost = postService.update(updatePostDto, files, tags, userId, postId);
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(
            method = "DELETE",
            summary = "게시글 삭제",
            description = "게시글을 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "게시글 삭제에 실패했습니다", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable long postId, @AuthenticatedUserId String userId) {
        postService.delete(postId, userId);
        return ResponseEntity.ok().build();
    }


    @Operation(
            method = "GET",
            summary = "게시글 조회",
            description = "게시글 하나를 가지고 온다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "게시글 조회에 실패했습니다.", content = @Content)
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.PostResponse> getPostByIdWithCommentsAndFiles(@PathVariable long postId) {
        return ResponseEntity.ok(postService.getPostByIdWithCommentsAndFiles(postId));
    }


    @Operation(
            method = "GET",
            summary = "게시글 리스트 조회",
            description = "게시글 리스트를 가지고 온다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.PostListDto.class))),
            @ApiResponse(responseCode = "400", description = "게시글 리스트 조회에 실패했습니다.", content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<PagingResponse<PostDto.PostListDto>> getPostList(SearchDto params) {
        PagingResponse<PostDto.PostListDto> postList = postService.getPostList(params);
        return ResponseEntity.ok(postList);
    }


    @Operation(
            method = "GET",
            summary = "인기글 게시글 리스트 조회",
            description = "좋아요기 10개 이상인 인기글 게시글 리스트를 가지고 온다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기글 게시글 리스트 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.PostListDto.class))),
            @ApiResponse(responseCode = "400", description = "인기글 게시글 리스트 조회에 실패했습니다.", content = @Content)
    })
    @GetMapping("/popular")
    public ResponseEntity<PagingResponse<PostDto.PostListDto>> getPopularPosts(SearchDto params) {
        PagingResponse<PostDto.PostListDto> popularPosts = postService.getPopularPosts(params);
        return ResponseEntity.ok(popularPosts);
    }
}
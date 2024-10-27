package com.study.api.post.service;

import com.study.api.fileUpload.service.S3Uploader;
import com.study.common.exception.authentication.NotAuthenticationException;
import com.study.common.exception.post.NotExistPostException;
import com.study.common.exception.user.NotFoundUserException;
import com.study.domain.blog.entity.Blog;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.mapper.blog.BlogQueryMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.mapper.post.PostCommandMapper;
import com.study.domain.mapper.post.PostQueryMapper;
import com.study.domain.post.dto.Pagination;
import com.study.domain.post.dto.PagingResponse;
import com.study.domain.post.dto.PostDto;
import com.study.domain.post.dto.SearchDto;
import com.study.domain.post.entity.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCommandMapper postCommandMapper;
    private final PostQueryMapper postQueryMapper;
    private final BlogUserQueryMapper blogUserQueryMapper;
    private final BlogQueryMapper blogQueryMapper;
    private final S3Uploader s3Uploader;

    @Transactional
    public PostDto.PostResponse register(@Valid PostDto.PostRegisterDto postRegisterDto, List<MultipartFile> files, String userId) {

        BlogUser blogUser = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));
        Blog blog = blogQueryMapper.findBlogByUserId(userId);


        log.info("blogUser={}", blogUser);
        List<String> postImageUrlList = null;

        if (files != null && !files.isEmpty()) {
            postImageUrlList = files.stream()
                    .map(file -> s3Uploader.upload(file, "post-image"))
                    .collect(Collectors.toList());
        }

        Post post = Post.of(postRegisterDto, blog.getId(), blogUser.getUserId(), blogUser.getNickName(), postImageUrlList);

        postCommandMapper.register(post);
        Post savedPost = postQueryMapper.findPostById(post.getId()).orElseThrow(NotExistPostException::new);

        if (postImageUrlList != null) {
            PostDto.RegisterFileDto registerFileDto = new PostDto.RegisterFileDto(post.getId(), postImageUrlList);
            postCommandMapper.registerImages(registerFileDto);
        }

        return PostDto.PostResponse.fromPost(post, List.of(), postImageUrlList);
    }


    @Transactional
    public PostDto.PostResponse update(@Valid PostDto.UpdatePostDto updatePostDto, List<MultipartFile> files, String userId, long postId) {

        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);

        validateWriter(post, userId);

        List<String> postImageUrlList = null;

        if (files != null && !files.isEmpty()) {
            postImageUrlList = files.stream()
                    .map(file -> s3Uploader.upload(file, "post-image"))
                    .collect(Collectors.toList());
        }
        postCommandMapper.deleteFilesByPostId(postId);
        postCommandMapper.update(updatePostDto.title(), updatePostDto.content(), postId);

        if (postImageUrlList != null && !postImageUrlList.isEmpty()) {
            postCommandMapper.updateFileUrls(postImageUrlList, postId);
        }

        Post upatedPost = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);

        return PostDto.PostResponse.fromPost(upatedPost, List.of(), postImageUrlList);
    }

    @Transactional
    public void delete(long postId, String userId) {
        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        validateWriter(post, userId);

        postCommandMapper.deleteFilesByPostId(post.getId());
        postCommandMapper.deleteByPostId(post);
    }

    @Transactional
    public PostDto.PostResponse getPostByIdWithAndFiles(long postId) {

        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        List<String> fileUrls = postQueryMapper.findFileUrlsByPostId(post.getId());
        return PostDto.PostResponse.fromPost(post, fileUrls);
    }



    @Transactional(readOnly = true)
    public PagingResponse<PostDto.PostListDto> getPostList(SearchDto params) {

        // 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환
        int count = postQueryMapper.count(params);
        log.info("count = {}", count);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }

        // Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        // 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 데이터 조회 후 응답 데이터 반환
        List<Post> list = postQueryMapper.findAll(params);

        List<PostDto.PostListDto> postListDtos = list.stream()
                .map(PostDto.PostListDto::of)
                .collect(Collectors.toList());

        log.info("postList = {}", list);
        return new PagingResponse<>(postListDtos, pagination);
    }

    private void validateWriter(Post post, String userId) {
        if (!post.getUserId().equals(userId)) {
            throw new NotAuthenticationException(userId);
        }
    }
}

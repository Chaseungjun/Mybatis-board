package com.study.api.post.service;

import com.study.api.fileUpload.service.S3Uploader;
import com.study.common.exception.authentication.NotAuthenticationException;
import com.study.common.exception.post.NotExistPostException;
import com.study.common.exception.user.NotFoundUserException;
import com.study.domain.blog.entity.Blog;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.comment.dto.CommentDto;
import com.study.domain.comment.entity.Comment;
import com.study.domain.mapper.blog.BlogQueryMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.mapper.comment.CommentCommandMapper;
import com.study.domain.mapper.comment.CommentQueryMapper;
import com.study.domain.mapper.post.PostCommandMapper;
import com.study.domain.mapper.post.PostQueryMapper;
import com.study.domain.mapper.postTag.PostTagCommandMapper;
import com.study.domain.mapper.tag.TagCommandMapper;
import com.study.domain.mapper.tag.TagQueryMapper;
import com.study.domain.post.dto.Pagination;
import com.study.domain.post.dto.PagingResponse;
import com.study.domain.post.dto.PostDto;
import com.study.domain.post.dto.SearchDto;
import com.study.domain.post.entity.Post;
import com.study.domain.redis.popularPost.service.PopularPostCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCommandMapper postCommandMapper;
    private final PostQueryMapper postQueryMapper;
    private final BlogUserQueryMapper blogUserQueryMapper;
    private final BlogQueryMapper blogQueryMapper;
    private final CommentCommandMapper commentCommandMapper;
    private final CommentQueryMapper commentQueryMapper;
    private final TagCommandMapper tagCommandMapper;
    private final TagQueryMapper tagQueryMapper;
    private final PostTagCommandMapper postTagCommandMapper;
    private final S3Uploader s3Uploader;
    private final PopularPostCacheService popularPostCacheService;

    /**
     * 게시글을 등록합니다.
     *
     * @param postRegisterDto 게시글 등록에 필요한 데이터
     * @param files 첨부 파일 리스트
     * @param tags 태그 리스트
     * @param userId 사용자 ID
     * @return 등록된 게시글의 응답 데이터
     */
    @Transactional
    public PostDto.PostResponse register(@Valid PostDto.PostRegisterDto postRegisterDto, List<MultipartFile> files, List<String> tags, String userId) {

        BlogUser blogUser = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));
        Blog blog = blogQueryMapper.findBlogByUserId(userId);
        List<String> postImageUrlList = null;

        if (files != null && !files.isEmpty()) {
            postImageUrlList = files.stream()
                    .map(file -> s3Uploader.upload(file, "post-image"))
                    .collect(Collectors.toList());
        }

        Post post = Post.of(postRegisterDto, blog.getId(), blogUser.getUserId(), blogUser.getNickName(), postImageUrlList);

        postCommandMapper.register(post);
        Post savedPost = postQueryMapper.findPostById(post.getId()).orElseThrow(NotExistPostException::new);
        registerTag(tags, savedPost);

        if (postImageUrlList != null) {
            PostDto.RegisterFileDto registerFileDto = new PostDto.RegisterFileDto(post.getId(), postImageUrlList);
            postCommandMapper.registerImages(registerFileDto);
        }
        return PostDto.PostResponse.fromPost(post, List.of(), postImageUrlList);
    }


    /**
     * 게시글을 수정합니다.
     *
     * @param updatePostDto 게시글 수정에 필요한 데이터
     * @param files 첨부 파일 리스트
     * @param tags 태그 리스트
     * @param userId 사용자 ID
     * @param postId 게시글 ID
     * @return 수정된 게시글의 응답 데이터
     */
    @Transactional
    public PostDto.PostResponse update(@Valid PostDto.UpdatePostDto updatePostDto, List<MultipartFile> files, List<String> tags, String userId, long postId) {

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

        postTagCommandMapper.deletePostTagsByPostId(post.getId());
        registerTag(tags, post);

        Post upatedPost = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);

        return PostDto.PostResponse.fromPost(upatedPost, List.of(), postImageUrlList);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void delete(long postId, String userId) {
        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        validateWriter(post, userId);

        commentCommandMapper.deleteByPostId(post.getId());
        postCommandMapper.deleteFilesByPostId(post.getId());
        postCommandMapper.deleteByPostId(post);
    }

    /**
     * 게시글 ID로 게시글과 댓글, 파일 정보를 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 게시글 응답 데이터
     */
    @Transactional
    public PostDto.PostResponse getPostByIdWithCommentsAndFiles(long postId) {


        PostDto.PostResponse cachedPopularPost = popularPostCacheService.getCachedPopularPost(postId);
        if (cachedPopularPost != null) {
            log.info("get cachedPopularPost = {}", cachedPopularPost);
            postCommandMapper.plusViewCount(postId);
            return popularPostCacheService.getCachedPopularPost(postId);
        }

        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        List<String> fileUrls = postQueryMapper.findFileUrlsByPostId(post.getId());
        List<Comment> comments = commentQueryMapper.getCommentsByPostId(post.getId());

        List<CommentDto.CommentResponseDto> commentResponseDtos = comments.stream()
                .map(CommentDto.CommentResponseDto::of)
                .collect(Collectors.toList());

        postCommandMapper.plusViewCount(postId);
        return PostDto.PostResponse.fromPost(post, commentResponseDtos, fileUrls);
    }


    /**
     * 게시글 리스트를 조회합니다.
     *
     * @param params 검색 조건
     * @return 페이징 처리된 게시글 리스트 응답 데이터
     */
    @Transactional(readOnly = true)
    public PagingResponse<PostDto.PostListDto> getPostList(SearchDto params) {

        // 일반 게시글일 경우 id 기준으로 내림차순 정렬
        if (params.getIsPopular() != null && params.getIsPopular()) {
            params.setSortBy("likeCount"); // 인기글일 경우 좋아요 수로 정렬
        } else {
            params.setSortBy("id"); // 일반 게시글일 경우 id로 정렬
        }

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

        return new PagingResponse<>(postListDtos, pagination);
    }

    /**
     * 인기 게시글 리스트를 조회합니다.
     *
     * @param params 검색 조건
     * @return 페이징 처리된 인기 게시글 리스트 응답 데이터
     */
    @Transactional(readOnly = true)
    public PagingResponse<PostDto.PostListDto> getPopularPosts(SearchDto params) {
        params.setIsPopular(true); // 인기글만 조회하도록 설정
        log.info("params.getIsPopular() = {}", params.getIsPopular());
        return getPostList(params); // 기존 getPostList 메서드 호출
    }

    /**
     * 특정 태그로 게시글을 조회합니다.
     *
     * @param tagName 태그 이름
     * @param params 검색 조건
     * @return 페이징 처리된 태그 기반 게시글 리스트 응답 데이터
     */
    @Transactional(readOnly = true)
    public PagingResponse<PostDto.PostListDto> getPostsByTag(String tagName, SearchDto params) {
        int tagId = tagQueryMapper.findTagIdByName(tagName);

        int count = postQueryMapper.countPostsByTagId(tagId);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }

        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        List<Post> posts = postQueryMapper.findPostsByTagId(tagId);
        List<PostDto.PostListDto> postListDtos = posts.stream()
                .map(PostDto.PostListDto::of)
                .collect(Collectors.toList());

        return new PagingResponse<>(postListDtos, pagination);
    }

    /**
     * 게시글 작성자를 검증합니다.
     *
     * @param post 게시글 정보
     * @param userId 검증할 사용자 ID
     */
    private void validateWriter(Post post, String userId) {
        if (!post.getUserId().equals(userId)) {
            throw new NotAuthenticationException(userId);
        }
    }

    /**
     * 주어진 태그 리스트에 대해 새로운 태그를 등록하고, 해당 태그를 게시글에 연결하는 메서드입니다.
     *
     * 1. 태그 리스트가 비어있거나 null일 경우 아무 작업도 수행하지 않습니다.
     * 2. 기존에 존재하는 태그를 조회하고, 새로 추가할 태그들만 필터링하여 등록합니다.
     * 3. 태그 이름에 해당하는 태그 ID를 조회한 후, 게시글에 해당 태그들을 연결합니다.
     *
     * @param tags 게시글에 추가할 태그들의 리스트. null 또는 비어있는 리스트는 처리되지 않습니다.
     * @param post 태그를 등록할 게시글. 해당 게시글에 태그를 연결합니다.
     */
    private void registerTag(List<String> tags, Post post) {

        if (tags == null || tags.isEmpty()) {
            return;
        }
        List<String> existingTagNames = tagQueryMapper.findExistingTagNames(tags);
        List<String> newTags = tags.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .collect(Collectors.toList());

        if (!newTags.isEmpty()) {
            tagCommandMapper.registerTag(newTags);
        }

        List<Integer> tagsId = tagQueryMapper.findTagIdsByNames(tags);
        if (!tagsId.isEmpty()) {
            postTagCommandMapper.registerPostTags(post.getId(), tagsId);
        }
    }
}
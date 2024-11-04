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

    @Transactional
    public void delete(long postId, String userId) {
        Post post = postQueryMapper.findPostById(postId).orElseThrow(NotExistPostException::new);
        validateWriter(post, userId);

        commentCommandMapper.deleteByPostId(post.getId());
        postCommandMapper.deleteFilesByPostId(post.getId());
        postCommandMapper.deleteByPostId(post);
    }

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

    @Transactional(readOnly = true)
    public PagingResponse<PostDto.PostListDto> getPopularPosts(SearchDto params) {
        params.setIsPopular(true); // 인기글만 조회하도록 설정
        log.info("params.getIsPopular() = {}", params.getIsPopular());
        return getPostList(params); // 기존 getPostList 메서드 호출
    }

    private void validateWriter(Post post, String userId) {
        if (!post.getUserId().equals(userId)) {
            throw new NotAuthenticationException(userId);
        }
    }

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
package com.study.domain.mapper.post;

import com.study.domain.post.dto.SearchDto;
import com.study.domain.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * PostQueryMapper는 게시물과 관련된 데이터베이스 조회 작업을 수행하는 MyBatis 매퍼 인터페이스입니다.
 * 게시물 정보를 ID로 조회하거나, 특정 태그와 관련된 게시물들을 조회하는 등의 기능을 제공합니다.
 */
@Mapper
public interface PostQueryMapper {

    /**
     * 주어진 게시물 ID에 대해 해당 게시물을 조회합니다.
     *
     * @param id 조회할 게시물의 ID
     * @return 주어진 게시물 ID에 해당하는 {@link Post} 객체, 없으면 빈 {@link Optional}
     */
    Optional<Post> findPostById(long id);

    /**
     * 주어진 게시물 ID에 대해 해당 게시물에 포함된 파일 URL들을 조회합니다.
     *
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 포함된 파일 URL 목록
     */
    List<String> findFileUrlsByPostId(long postId);

    /**
     * 주어진 검색 파라미터를 기반으로 게시물 목록을 조회합니다.
     *
     * @param params 검색 파라미터 {@link SearchDto}
     * @return 검색 조건에 맞는 게시물 목록
     */
    List<Post> findAll(SearchDto params);

    /**
     * 주어진 검색 파라미터를 기반으로 게시물의 총 개수를 조회합니다.
     *
     * @param params 검색 파라미터 {@link SearchDto}
     * @return 검색 조건에 맞는 게시물의 개수
     */
    int count(SearchDto params);

    /**
     * 주어진 태그 ID에 해당하는 게시물 목록을 조회합니다.
     *
     * @param tagId 조회할 태그의 ID
     * @return 주어진 태그 ID와 관련된 게시물 목록
     */
    List<Post> findPostsByTagId(int tagId);

    /**
     * 주어진 태그 ID에 해당하는 게시물의 총 개수를 조회합니다.
     *
     * @param tagId 조회할 태그의 ID
     * @return 주어진 태그 ID와 관련된 게시물의 개수
     */
    int countPostsByTagId(int tagId);
}

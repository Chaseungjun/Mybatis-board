package com.study.domain.mapper.tag;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * TagQueryMapper는 태그 관련 데이터베이스 조회 작업을 수행하는 MyBatis 매퍼 인터페이스입니다.
 * 태그의 ID를 이름으로 조회하거나, 주어진 태그들에 대한 기존 태그 이름을 확인하는 메서드를 제공합니다.
 */
@Mapper
public interface TagQueryMapper {

    /**
     * 주어진 태그 이름 목록에 대해 해당하는 태그 ID 목록을 조회합니다.
     *
     * @param tags 태그 이름 목록
     * @return 태그 이름에 해당하는 ID 목록
     */
    List<Integer> findTagIdsByNames(List<String> tags);

    /**
     * 주어진 태그 이름 목록에 대해 데이터베이스에 존재하는 태그 이름 목록을 조회합니다.
     *
     * @param tags 확인할 태그 이름 목록
     * @return 데이터베이스에 존재하는 태그 이름 목록
     */
    List<String> findExistingTagNames(List<String> tags);

    /**
     * 주어진 태그 이름에 대해 해당하는 태그 ID를 조회합니다.
     *
     * @param tagName 조회할 태그 이름
     * @return 주어진 태그 이름에 해당하는 태그 ID
     */
    int findTagIdByName(String tagName);
}

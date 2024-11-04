package com.study.domain.mapper.tag;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagQueryMapper {

    List<Integer> findTagIdsByNames(List<String> tags);

    List<String> findExistingTagNames(List<String> tags);

}

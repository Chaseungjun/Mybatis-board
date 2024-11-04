package com.study.domain.mapper.tag;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TagCommandMapper {

    void registerTag(@Param("tags") List<String> tags);

}
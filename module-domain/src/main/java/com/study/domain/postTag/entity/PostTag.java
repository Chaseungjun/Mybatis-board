package com.study.domain.postTag.entity;

import lombok.Getter;

/**
 * 게시글과 태그의 매핑을 나타내는 엔티티 클래스입니다.
 */
@Getter
public class PostTag {

    private long postId;
    private int tagId;
}

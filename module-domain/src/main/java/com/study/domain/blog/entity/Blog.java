package com.study.domain.blog.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 블로그를 나타내는 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blog {

    /**
     * 블로그의 고유 식별자입니다.
     */
    private long id;


    /**
     * 블로그 유저의 ID
     */
    private String userId;

    /**
     * 블로그 생성 시각
     */
    private LocalDateTime createdDate;

    /**
     * 블로그 수정 시각
     */
    private LocalDateTime modifiedDate;

    /**
     * 블로그 삭제 시각
     */
    private LocalDateTime deletedDate;


    /**
     * 주어진 블로그 사용자로 새로운 블로그를 생성합니다.
     *
     * @param userId 블로그 사용자의 ID
     */
    public Blog(String userId) {
        this.userId = userId;
    }


    /**
     * 주어진 블로그 사용자로 새로운 블로그를 생성하는 정적팩토리메서드 입니다.
     *
     * @param userId 블로그 사용자의 식별자 id
     */
    public static Blog from(String userId) {
        return new Blog(userId);
    }
}

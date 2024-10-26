package com.study.domain.redis.refreshToken.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


/**
 *  Redis에 저장되는 리프레시 토큰 엔티티를 정의합니다.
 */
@Getter
@RedisHash(value = "refreshToken", timeToLive = 604800) // 이 클래스가 Redis 해시(hash) 구조로 저장될 것임을 나타낸다. value 속성은 Redis 해시의 이름을 지정한다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    /**
     * 사용자 ID로, Redis 해시의 키로 사용됩니다. "refreshToken:{userId}"와 같은 키가 생성됩니다.
     */
    @Id
    private String userId;


    /**
     * 리프레시 토큰의 값으로, 인덱스가 생성됩니다.
     */
    private String value;

    /**
     * RefreshToken 생성자.
     *
     * @param userId 사용자 ID
     * @param value 리프레시 토큰의 값
     */
    @Builder
    public RefreshToken(String userId, String value) {
        this.userId = userId;
        this.value = value;
    }

}

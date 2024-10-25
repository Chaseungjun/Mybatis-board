package com.study.domain.redis.refreshToken.repository;

import com.study.domain.redis.refreshToken.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


/**
 * RefreshTokenRepository는 리프레시 토큰을 관리하기 위한 저장소 인터페이스로
 * 리프레시 토큰을 값으로 조회하고 존재 여부를 확인하는 메서드를 제공합니다.
 *
 */
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    /**
     * 주어진 값으로 리프레시 토큰을 조회합니다.
     *
     * @param value 리프레시 토큰의 값
     * @return 값이 일치하는 리프레시 토큰을 포함한 Optional 객체
     */
    Optional<RefreshToken> findByValue(String value);


    /**
     * 주어진 값이 존재하는지 확인합니다.
     *
     * @param value 리프레시 토큰의 값
     * @return 값이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByValue(String value);
}

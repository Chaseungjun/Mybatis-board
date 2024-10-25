package com.study.domain.redis.refreshToken.repository;

import com.study.domain.redis.refreshToken.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenQuery{

    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * 주어진 값으로 리프레시 토큰을 조회합니다.
     *
     * @param value 리프레시 토큰의 값
     * @return 값이 일치하는 리프레시 토큰을 포함한 Optional 객체
     */
    public Optional<RefreshToken> findByValue(String value) {
        return refreshTokenRepository.findByValue(value);
    }

    /**
     * 주어진 값이 존재하는지 확인합니다.
     *
     * @param value 리프레시 토큰의 값
     * @return 값이 존재하면 true, 그렇지 않으면 false
     */
    public boolean existsByValue(String value) {
        return refreshTokenRepository.existsByValue(value);
    }
}

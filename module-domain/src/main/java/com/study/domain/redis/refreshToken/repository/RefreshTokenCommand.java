package com.study.domain.redis.refreshToken.repository;

import com.study.domain.redis.refreshToken.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * RefreshTokenCommand는 리프레시 토큰을 관리하기 위한 서비스 클래스로
 * 이 클래스는 리프레시 토큰을 저장하고 삭제하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenCommand {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 리프레시 토큰을 저장합니다. 저장하기 전에 리프레시 토큰의 TTL(Time-To-Live)을 업데이트합니다.
     *
     * @param refreshToken 저장할 리프레시 토큰
     * @return 저장된 리프레시 토큰
     */
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }


    public void deleteById(String userId) {
        refreshTokenRepository.deleteById(userId);
    }

}

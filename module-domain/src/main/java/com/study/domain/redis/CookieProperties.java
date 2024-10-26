package com.study.domain.redis;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
    설정 파일에서 쿠키 관련 프로퍼티를 로드하여 관리하는 클래스입니다.
 */
@Getter
@Validated
@ConfigurationProperties(prefix = "custom.cookie")
@RequiredArgsConstructor
public class CookieProperties {

    /**
     * 쿠키의 도메인을 나타내는 프로퍼티입니다.
     */
    @NotNull
    private final String domain;

    /**
     * 쿠키의 SameSite 속성을 나타내는 프로퍼티입니다.
     */
    @NotNull
    private final String sameSite;
}

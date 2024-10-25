package com.study.api.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * JWT 설정을 관리하는 클래스입니다.
 */
@Component
@Getter
public class JwtProperties {


    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds}")
    private Long accessTokenValidTime;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidTime;

    @Value("${jwt.jwt-issuer}")
    private String issuer;

    @Value("${jwt.token-start-with}")
    private String tokenPrefix;

    @Value("${jwt.header}")
    private String header;

}

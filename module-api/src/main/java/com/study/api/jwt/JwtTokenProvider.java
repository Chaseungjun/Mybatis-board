package com.study.api.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰을 생성하는 클래스입니다.
 */
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    public static final String AUTHORITIES_KEY = "ROLE";
    public static final String TOKEN_TYPE_KEY = "type";
    public static final String AUTHORITIES_DELIMITER = "<?>";

    /**
     * 권한 목록을 하나의 문자열로 변환합니다.
     *
     * @param grantedAuthorities Spring Security에서 권한을 나타내는 GrantedAuthority 객체들의 컬렉션
     * @return 권한 목록을 문자열로 변환한 값
     */
    private static String toString(final Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(AUTHORITIES_DELIMITER));
    }

    /**
     * 사용자 인증 정보를 바탕으로 액세스 토큰을 생성합니다.
     *
     * @param authentication 사용자 인증 정보
     * @return 생성된 액세스 토큰
     */
    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + jwtProperties.getAccessTokenValidTime() * 1000); // 유효 시간을 밀리초로 변환

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim(TOKEN_TYPE_KEY, TokenType.ACCESS)
                .claim(AUTHORITIES_KEY, toString(authentication.getAuthorities()))
                .setSubject(authentication.getName())
                .setIssuer(jwtProperties.getIssuer())
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     *
     * @return 생성된 리프레시 토큰
     */
    public String createRefreshToken() {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + jwtProperties.getRefreshTokenValidTime() * 1000);  // 유효 시간을 밀리초로 변환

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim(TOKEN_TYPE_KEY, TokenType.REFRESH)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .setExpiration(expiredAt)
                .compact();
    }
}

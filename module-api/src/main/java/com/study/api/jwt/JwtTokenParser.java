package com.study.api.jwt;

import com.study.domain.redis.refreshToken.repository.RefreshTokenQuery;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


/**
 * JWT 토큰을 파싱하고 인증 정보를 추출하는 클래스입니다.
 */
@Component
public class JwtTokenParser {


    private final RefreshTokenQuery refreshTokenQuery;
    private final JwtParser jwtParser;

    /**
     * JwtTokenParser 생성자.
     *
     * @param jwtProperties JWT 설정 정보
     * @param refreshTokenQuery 리프레시 토큰 조회 서비스
     */
    public JwtTokenParser(
            RefreshTokenQuery refreshTokenQuery,
            JwtProperties jwtProperties
            ) {
        this.refreshTokenQuery = refreshTokenQuery;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build();
    }

    /**
     * JWT 토큰에서 인증 정보를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 인증 정보
     */
        public Authentication extractAuthentication(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(JwtTokenProvider.AUTHORITIES_KEY).toString().split(JwtTokenProvider.AUTHORITIES_DELIMITER))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = User.builder()
                    .username(claims.getSubject())
                    .password("N/A")
                    .authorities(authorities)
                    .build();
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (JwtException | IllegalArgumentException | NullPointerException exception) {
            throw new BadCredentialsException(exception.getMessage());
        }
    }


    /**
     * 액세스 토큰이 유효한지 검증합니다.
     *
     * @param token 액세스 토큰
     * @return 유효한 경우 true, 그렇지 않은 경우 false
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, TokenType.ACCESS);
    }

    /**
     * 리프레시 토큰이 유효한지 검증합니다.
     *
     * @param token 리프레시 토큰
     * @return 유효한 경우 true, 그렇지 않은 경우 false
     */
    public boolean validateRefreshToken(String token) {
        return refreshTokenQuery.existsByValue(token) && validateToken(token, TokenType.REFRESH);
    }


    /**
     * 토큰이 유효한지 검증합니다.
     *
     * @param token JWT 토큰
     * @param tokenType 토큰 타입
     * @return 유효한 경우 true, 그렇지 않은 경우 false
     */
    private boolean validateToken(String token, TokenType tokenType) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            boolean isTokenExpired = claims.getExpiration().before(new Date()); // true : 토큰이 만료됨
            boolean isTokenTypeMatch = tokenType.name().equals(claims.get(JwtTokenProvider.TOKEN_TYPE_KEY));
            return !isTokenExpired && isTokenTypeMatch;

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
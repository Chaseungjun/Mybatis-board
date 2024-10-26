package com.study.api.blogUser.service;

import com.study.api.jwt.JwtProperties;
import com.study.api.jwt.JwtTokenProvider;
import com.study.api.jwt.TokenType;

import com.study.common.exception.user.NotFoundUserException;
import com.study.common.utill.CookieUtil;
import com.study.domain.blogUser.dto.BlogUserDto;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.redis.CookieProperties;
import com.study.domain.redis.refreshToken.entity.RefreshToken;
import com.study.domain.redis.refreshToken.repository.RefreshTokenCommand;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * 사용자 로그인 요청을 처리하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogUserSignInService {

    private final BlogUserQueryMapper blogUserQueryMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenCommand refreshTokenCommand;
    private final CookieProperties cookieProperties;


    /**
     * 사용자의 로그인 요청을 처리합니다.
     *
     * @param response   HTTP 응답 객체
     * @param requestDto 로그인 요청 정보
     */
    public void signIn(
            HttpServletResponse response,
            BlogUserDto.BlogUserSignInRequestDto requestDto
    ) {
        BlogUser blogUser = blogUserQueryMapper.findBlogUserByUserId(requestDto.userId()).orElseThrow(() -> new NotFoundUserException(requestDto.userId()));

        if (blogUser.getDeleted_at() != null){
            throw new NotFoundUserException(requestDto.userId());
        }

        if (passwordEncoder.matches(requestDto.password(), blogUser.getPassword())) {
            Authentication authenticate = authenticateUser(requestDto.userId(), requestDto.password());

            String accessTokenValue = createAccessToken(authenticate);
            String refreshTokenValue = createRefreshToken();

            saveRefreshToken(authenticate.getName(), refreshTokenValue);
            addTokenCookie(response, TokenType.REFRESH, refreshTokenValue, jwtProperties.getRefreshTokenValidTime());
            response.setHeader("Authorization", accessTokenValue);
            log.info("accessTokenValue = {}", accessTokenValue);
            log.info("refreshTokenValue = {}", refreshTokenValue);
        }
    }

    /**
     * 사용자를 인증합니다.
     *
     * @param userId   사용자 ID
     * @param password 사용자 비밀번호
     * @return 인증된 사용자 정보
     */
    private Authentication authenticateUser(String userId, String password) {
        return authenticationManagerBuilder.getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(userId, password));
    }

    /**
     * Access Token을 생성합니다.
     *
     * @param authentication 인증 정보
     * @return 생성된 Access Token 값
     */
    private String createAccessToken(Authentication authentication) {
        return jwtTokenProvider.createAccessToken(authentication);
    }

    /**
     * Refresh Token을 생성합니다.
     *
     * @return 생성된 Refresh Token 값
     */
    private String createRefreshToken() {
        return jwtTokenProvider.createRefreshToken();
    }

    /**
     * Refresh Token을 저장합니다.
     *
     * @param userId     사용자 ID
     * @param tokenValue Refresh Token 값
     */
    private void saveRefreshToken(String userId, String tokenValue) {
        refreshTokenCommand.save(new RefreshToken(userId, tokenValue));
    }

    /**
     * 토큰을 쿠키에 추가합니다.
     *
     * @param response      HTTP 응답 객체
     * @param tokenType     토큰 타입 (ACCESS, REFRESH)
     * @param tokenValue    토큰 값
     * @param tokenLifeTime 토큰 유효 기간
     */
    private void addTokenCookie(
            HttpServletResponse response,
            TokenType tokenType,
            String tokenValue,
            long tokenLifeTime
    ) {
        CookieUtil.addCookie(
                response,
                tokenType.name(), // 쿠키이름
                tokenValue,  // 쿠키 값
                tokenLifeTime,
                cookieProperties.getDomain(),
                cookieProperties.getSameSite()
        );
    }
}

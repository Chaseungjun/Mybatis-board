package com.study.api.jwt.filter;

import com.study.api.jwt.JwtProperties;
import com.study.api.jwt.JwtTokenParser;
import com.study.api.jwt.JwtTokenProvider;
import com.study.api.jwt.TokenType;
import com.study.api.permission.service.PermissionService;
import com.study.common.exception.user.NotFoundUserException;
import com.study.common.utill.CookieUtil;
import com.study.domain.blog.entity.Blog;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.mapper.blog.BlogQueryMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import com.study.domain.permission.entity.UserRole;
import com.study.domain.redis.CookieProperties;
import com.study.domain.redis.refreshToken.entity.RefreshToken;
import com.study.domain.redis.refreshToken.repository.RefreshTokenQuery;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.builder;

/**
 * JWT 토큰 필터를 구현한 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenParser jwtTokenParser;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlogUserQueryMapper blogUserQueryMapper;
    private final BlogQueryMapper blogQueryMapper;
    private final RefreshTokenQuery refreshTokenQuery;
    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;
    private final PermissionService permissionService;


    /**
     * 요청을 필터링하여 JWT 토큰을 검증하고, 사용자 인증을 처리합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractBearerToken(request);
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookie(request, TokenType.REFRESH.name());
        String refreshToken = refreshTokenCookie.map(Cookie::getValue).orElse("");

        if (jwtTokenParser.validateAccessToken(accessToken)) {
            Authentication authentication = jwtTokenParser.extractAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);  // 인증 정보를 추출하고, SecurityContextHolder에 설정합니다.

        } else if (jwtTokenParser.validateRefreshToken(refreshToken)) {

            String userId = findUserIdByRefreshToken(refreshToken);
            Blog blog = findBlogByUserId(userId);

            TokenPair tokenPair = reissueTokenByUserId(userId, blog);

            Authentication authentication = jwtTokenParser.extractAuthentication(tokenPair.accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            response.setHeader("Authorization", "Bearer " + tokenPair.accessToken());

            CookieUtil.addCookie(
                    response,
                    TokenType.REFRESH.name(),
                    tokenPair.refreshToken(),
                    jwtProperties.getRefreshTokenValidTime(),
                    cookieProperties.getDomain(),
                    cookieProperties.getSameSite());
        } else {
            SecurityContextHolder.clearContext();
            CookieUtil.deleteCookie(response, TokenType.REFRESH.name(), cookieProperties.getDomain());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Refresh Token을 통해 사용자 ID를 찾습니다.
     *
     * @param refreshToken Refresh Token 값
     * @return 사용자 ID
     */
    private String findUserIdByRefreshToken(String refreshToken) {
        return refreshTokenQuery.findByValue(refreshToken)
                .map(RefreshToken::getUserId)
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 사용자 ID를 통해 토큰을 재발급합니다.
     *
     * @param userId 사용자 ID
     * @param blog 사용자의 블로그 정보
     * @return 재발급된 토큰 access, refresh token 한 쌍
     */
    private TokenPair reissueTokenByUserId(String userId, Blog blog) {
        BlogUser user = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));
        UserRole userRole = permissionService.determineUserRole(blog, user);

        UserRole role = userRole;

        UserDetails userDetails = builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(role.name())))
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newRefreshToken = jwtTokenProvider.createRefreshToken();
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    /**
     * 요청에서 Bearer 토큰을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return Bearer를 제거한 토큰 값
     */
    private String extractBearerToken(HttpServletRequest request) {
        String BearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(BearerToken) && BearerToken.startsWith(jwtProperties.getTokenPrefix())) {
            return BearerToken.substring(jwtProperties.getTokenPrefix().length());
        }
        return null;
    }

    /**
     * 사용자 ID를 통해 블로그를 찾습니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 블로그 정보
     */
    private Blog findBlogByUserId(String userId) {
        BlogUser user = blogUserQueryMapper.findBlogUserByUserId(userId).orElseThrow(() -> new NotFoundUserException(userId));
        return blogQueryMapper.findBlogByUserId(user.getUserId());
    }

    /**
     * 토큰 쌍을 나타내는 레코드 클래스입니다.
     *
     * @param accessToken Access Token 값
     * @param refreshToken Refresh Token 값
     */
    private record TokenPair(String accessToken, String refreshToken) {}
}

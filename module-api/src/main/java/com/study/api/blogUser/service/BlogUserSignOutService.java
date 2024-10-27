package com.study.api.blogUser.service;

import com.study.api.jwt.TokenType;
import com.study.common.utill.CookieUtil;
import com.study.domain.redis.CookieProperties;
import com.study.domain.redis.refreshToken.repository.RefreshTokenCommand;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogUserSignOutService {

    private final RefreshTokenCommand refreshTokenCommand;
    private final CookieProperties cookieProperties;

    public void signOut(HttpServletResponse response, String userId) {
        refreshTokenCommand.deleteById(userId);
        deleteTokenCookie(response, TokenType.REFRESH);
    }

    private void deleteTokenCookie(
            HttpServletResponse response,
            TokenType tokenType
    ) {
        CookieUtil.deleteCookie(response, tokenType.name(), cookieProperties.getDomain());
    }
}

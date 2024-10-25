package com.study.api.jwt.handler;

import com.study.common.exception.JwtErrorResponse;
import com.study.common.utill.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 사용자가 인증되지 않았거나 유효한 인증 정보가 부족한 경우를 처리하는 핸들러 클래스입니다.
 */
@Component
public class JwtAuthenticationEntryPointHandler implements AuthenticationEntryPoint {


    /**
     * 인증 과정에서 예외가 발생했을 때 호출되는 메서드입니다.
     *
     * @param request       HTTP 요청 객체
     * @param response      HTTP 응답 객체
     * @param authException 인증 예외
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        JwtErrorResponse responseBody = JwtErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "인증 과정에 문제가 발생했습니다.",
                request.getRequestURI());

        String errorResponse = JsonUtils.convertObjectToJson(responseBody);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorResponse);
    }
}

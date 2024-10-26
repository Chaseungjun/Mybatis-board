package com.study.api.jwt.handler;

import com.study.common.exception.JwtErrorResponse;
import com.study.common.utill.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증이 완료되었으나 해당 엔드포인트에 접근할 권한이 없는 경우 핸들러 클래스입니다.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 접근이 거부되었을 때 호출되는 메서드입니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        JwtErrorResponse responseBody = JwtErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "권한이 없습니다.",
                    request.getRequestURI());

        response.getWriter().write(JsonUtils.convertObjectToJson(responseBody));

    }
}

package com.study.common.utill;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseCookie;

import java.io.*;
import java.util.Base64;
import java.util.Optional;

/**
 * 유틸리티 클래스로, HTTP 쿠키 관련 기능을 제공합니다.
 */
@UtilityClass
public final class CookieUtil {

    /**
     * HttpServletRequest에서 지정된 이름의 쿠키를 찾아서 Optional로 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @param name 쿠키 이름
     * @return Optional<Cookie>
     */
    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * HttpServletResponse에 쿠키를 추가합니다.
     *
     * @param response HTTP 응답 객체
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * @param maxAge 쿠키의 유효 시간 (초)
     * @param domain 쿠키의 도메인
     * @param sameSite SameSite 설정 ("Strict", "Lax", "None" 중 하나)
     */
    public void addCookie(HttpServletResponse response, String name, String value, long maxAge, String domain, String sameSite) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .path("/")
                .domain(domain)
                .httpOnly(true)
                .secure(true)
                .sameSite(sameSite)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * HttpServletRequest와 HttpServletResponse에서 지정된 이름의 쿠키를 삭제합니다.
     *
     * @param response HTTP 응답 객체
     * @param name 쿠키 이름
     * @param domain 쿠키의 도메인
     */
    public void deleteCookie(HttpServletResponse response, String name, String domain) {
        ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                .maxAge(0)
                .path("/")
                .domain(domain)
                .httpOnly(true)
                .secure(true)
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());
    }

    /**
     * 쿠키 값을 역직렬화하여 객체로 변환합니다.
     *
     * @param cookie 쿠키 객체
     * @param clazz 변환할 클래스 타입
     * @param <T> 변환할 객체의 타입
     * @return 변환된 객체
     */
    public <T> T deserialize(Cookie cookie, Class<T> clazz) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cookie.getValue());

            try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(decodedBytes))) {
                return clazz.cast(inputStream.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 객체를 직렬화하여 쿠키 값으로 변환합니다.
     *
     * @param object 직렬화할 객체
     * @return 직렬화된 쿠키 값
     */
    public String serialize(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(object);
            }
            byte[] serializedBytes = byteArrayOutputStream.toByteArray();
            return Base64.getUrlEncoder().encodeToString(serializedBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

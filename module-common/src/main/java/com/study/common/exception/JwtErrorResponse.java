package com.study.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JWT 관련 작업 중 발생한 오류 응답을 나타내는 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtErrorResponse {

    /** 오류가 발생한 타임스탬프입니다. */
    private String timestamp;

    /** 오류와 연관된 HTTP 상태 코드입니다. */
    private int status;

    /** 상태 코드에 따른 HTTP 오류 메시지입니다. */
    private String error;

    /** 오류에 대한 상세 메시지입니다. */
    private String message;

    /** 오류가 발생한 요청 경로입니다. */
    private String path;

    /**
     * 주어진 세부 정보로 새로운 JwtErrorResponse 객체를 생성합니다.
     *
     * @param timestamp 오류 발생 시간대
     * @param status HTTP 상태 코드
     * @param error 상태 코드에 따른 HTTP 오류 메시지
     * @param message 오류에 대한 상세 메시지
     * @param path 오류가 발생한 요청 경로
     */
    private JwtErrorResponse(String timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * 현재 시간을 기준으로 초기화된 JwtErrorResponse 객체를 생성합니다.
     *
     * @param status HTTP 상태 코드
     * @param error 상태 코드에 따른 HTTP 오류 메시지
     * @param message 오류에 대한 상세 메시지
     * @param path 오류가 발생한 요청 경로
     * @return 새로 생성된 JwtErrorResponse 객체
     */
    public static JwtErrorResponse of(int status, String error, String message, String path) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new JwtErrorResponse(timestamp, status, error, message, path);
    }
}

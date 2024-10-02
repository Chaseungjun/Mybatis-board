package com.study.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * API 요청 처리 중 발생한 에러 응답을 담는 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    /**
     * HTTP 상태 코드
     */
    private int status;
    /**
     * 에러 코드
     */
    private String code;
    /**
     * 에러 메시지
     */
    private String message;



    /**
     * 주어진 ErrorCode로 ErrorResponse 객체를 생성합니다.
     *
     * @param errorCode 에러 코드
     */
    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 주어진 ErrorCode를 이용해 ErrorResponse 객체를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
}

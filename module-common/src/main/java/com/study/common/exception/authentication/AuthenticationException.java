package com.study.common.exception.authentication;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

/**
 * 인증 관련 예외를 나타내는 클래스입니다.
 */
public class AuthenticationException extends BusinessException {

    private String value;

    /**
     * 주어진 값과 에러 코드로 새로운 AuthenticationException을 생성합니다.
     *
     * @param value 예외 발생 시점의 값
     * @param errorCode 에러 코드
     */
    public AuthenticationException(String value, ErrorCode errorCode) {
        super(errorCode);
        this.value = value;
    }

    /**
     * 예외 발생 시점의 값을 반환합니다.
     *
     * @return 예외 발생 시점의 값
     */
    public String getValue() {
        return value;
    }
}


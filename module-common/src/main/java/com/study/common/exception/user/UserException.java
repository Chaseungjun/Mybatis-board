package com.study.common.exception.user;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

/**
 * 사용자 관련 예외를 나타내는 클래스입니다.
 */
public class UserException extends BusinessException {

    private String value;

    /**
     * 사용자 관련 예외를 생성합니다.
     *
     * @param value     예외 메시지나 값을 나타내는 문자열
     * @param errorCode 예외 코드
     */
    public UserException(String value, ErrorCode errorCode) {
        super(errorCode);
        this.value = value;
    }
}

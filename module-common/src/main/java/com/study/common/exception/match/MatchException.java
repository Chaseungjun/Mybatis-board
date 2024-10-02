package com.study.common.exception.match;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

/**
 * 일치하지 않음 예외를 나타내는 클래스입니다.
 */
public class MatchException extends BusinessException {

    private String value;

    /**
     * 주어진 값과 에러 코드로 새로운 MatchException을 생성합니다.
     *
     * @param value 일치하지 않은 값
     * @param errorCode 에러 코드
     */
    public MatchException(String value, ErrorCode errorCode) {
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

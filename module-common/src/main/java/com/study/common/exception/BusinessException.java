package com.study.common.exception;

/**
 * 비즈니스 로직에서 발생하는 예외를 처리하는 클래스입니다.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 주어진 에러 코드로 새로운 BusinessException을 생성합니다.
     *
     * @param errorCode 에러 코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

package com.study.common.exception.duplicate;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import lombok.Getter;


/**
 * 중복 예외를 나타내는 클래스입니다.
 */
@Getter
public class DuplicateException extends BusinessException {

    private String value;

    /**
     * 주어진 값과 에러 코드로 새로운 DuplicateException을 생성합니다.
     *
     * @param value 중복이 발생한 값
     * @param errorCode 에러 코드
     */
    public DuplicateException(String value, ErrorCode errorCode) {
        super(errorCode);
        this.value = value;
    }

}

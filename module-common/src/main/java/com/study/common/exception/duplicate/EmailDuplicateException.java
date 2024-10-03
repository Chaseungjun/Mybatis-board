package com.study.common.exception.duplicate;

import com.study.common.exception.ErrorCode;

/**
 * 이메일 중복 예외를 나타내는 클래스입니다.
 */
public class EmailDuplicateException extends DuplicateException {

    /**
     * 주어진 이메일 주소로 새로운 EmailDuplicateException을 생성합니다.
     *
     * @param email 중복된 이메일 주소
     */
    public EmailDuplicateException(String email) {
        super(email, ErrorCode.DUPLICATE_EMAIL);
    }
}

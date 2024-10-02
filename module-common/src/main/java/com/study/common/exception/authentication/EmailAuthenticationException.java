package com.study.common.exception.authentication;

import com.study.common.exception.ErrorCode;

/**
 * 이메일 인증 실패 예외를 나타내는 클래스입니다.
 */
public class EmailAuthenticationException extends AuthenticationException {

    /**
     * 주어진 이메일 주소로 새로운 EmailAuthenticationException을 생성합니다.
     *
     * @param email 인증 실패한 이메일 주소
     */
    public EmailAuthenticationException(String email) {
        super(email, ErrorCode.FAIL_AUTHENTICATION_EMAIL);
    }
}

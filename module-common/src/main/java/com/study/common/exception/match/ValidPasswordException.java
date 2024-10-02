package com.study.common.exception.match;

import com.study.common.exception.ErrorCode;

/**
 * 비밀번호 확인입력 예외를 나타내는 클래스입니다.
 */
public class ValidPasswordException extends MatchException {

    /**
     * 비밀번호 확인입력 예외를 생성합니다.
     *
     * @param password 비밀번호
     */
    public ValidPasswordException(String password) {
        super(password, ErrorCode.NOT_MATCH_PASSWORD);
    }
}

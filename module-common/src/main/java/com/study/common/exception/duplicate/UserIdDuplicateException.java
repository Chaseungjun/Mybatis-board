package com.study.common.exception.duplicate;

import com.study.common.exception.ErrorCode;

/**
 * 사용자 ID 중복 예외를 나타내는 클래스입니다.
 */
public class UserIdDuplicateException extends DuplicateException {

    /**
     * 주어진 사용자 ID로 새로운 UserIdDuplicateException을 생성합니다.
     *
     * @param userId 중복된 사용자 ID
     */
    public UserIdDuplicateException(final String userId) {
        super(userId, ErrorCode.DUPLICATE_USERID);
    }
}

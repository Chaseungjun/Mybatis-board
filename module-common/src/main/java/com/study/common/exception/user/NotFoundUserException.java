package com.study.common.exception.user;

import com.study.common.exception.ErrorCode;

/**
 * 사용자를 찾을 수 없음 예외를 나타내는 클래스입니다.
 */
public class NotFoundUserException extends UserException {

    /**
     * 사용자를 찾을 수 없음 예외를 생성합니다.
     *
     * @param userId 사용자 ID
     */
    public NotFoundUserException(String userId) {
        super(userId, ErrorCode.NOT_FOUND_USER);
    }

}

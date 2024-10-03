package com.study.common.exception.duplicate;

import com.study.common.exception.ErrorCode;

/**
 * 닉네임 중복 예외를 나타내는 클래스입니다.
 */
public class NickNameDuplicateException extends DuplicateException {

    /**
     * 주어진 닉네임으로 새로운 NickNameDuplicateException을 생성합니다.
     *
     * @param nickName 중복된 닉네임
     */
    public NickNameDuplicateException(String nickName) {
        super(nickName, ErrorCode.DUPLICATE_NICKNAME);
    }
}

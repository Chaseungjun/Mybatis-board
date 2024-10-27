package com.study.common.exception.user;


import com.study.common.exception.ErrorCode;

public class NotSignedInException extends UserException {
    public NotSignedInException(String userId) {
        super(userId, ErrorCode.NOT_SIGNED_IN);
    }
}

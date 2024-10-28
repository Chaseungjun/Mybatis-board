package com.study.common.exception.liked;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

public class LikedException extends BusinessException {

    public LikedException(ErrorCode errorCode) {
        super(errorCode);
    }
}

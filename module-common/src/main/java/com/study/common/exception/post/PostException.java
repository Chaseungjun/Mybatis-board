package com.study.common.exception.post;


import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

public class PostException extends BusinessException {

    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package com.study.common.exception.comment;


import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

public class CommentException extends BusinessException {
    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}

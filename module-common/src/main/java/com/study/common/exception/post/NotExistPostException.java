package com.study.common.exception.post;


import com.study.common.exception.ErrorCode;

public class NotExistPostException extends PostException {
    public NotExistPostException() {
        super(ErrorCode.NOT_EXIST_POST);
    }
}

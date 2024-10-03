package com.study.common.exception.upload;


import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

public class UploadException extends BusinessException {

    private String value;

    public UploadException(String value, ErrorCode errorCode) {
        super(errorCode);
        this.value = value;
    }
}

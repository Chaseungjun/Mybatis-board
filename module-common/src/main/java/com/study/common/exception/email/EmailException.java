package com.study.common.exception.email;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;

public class EmailException extends BusinessException {


    private String value;

    public EmailException(String value, ErrorCode errorCode) {
        super(errorCode);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

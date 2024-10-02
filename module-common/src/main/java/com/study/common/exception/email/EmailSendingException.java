package com.study.common.exception.email;

import com.study.common.exception.ErrorCode;

public class EmailSendingException extends EmailException{

    public EmailSendingException(String email, ErrorCode errorCode) {
        super(email, errorCode);
    }

}

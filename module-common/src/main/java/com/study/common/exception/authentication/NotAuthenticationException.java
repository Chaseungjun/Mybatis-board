package com.study.common.exception.authentication;


import com.study.common.exception.ErrorCode;

public class NotAuthenticationException extends AuthenticationException{
    public NotAuthenticationException(String value) {
        super(value, ErrorCode.DONT_HAVE_AUTHENTICATION);
    }
}

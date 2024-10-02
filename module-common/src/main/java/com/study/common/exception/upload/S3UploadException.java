package com.study.common.exception.upload;


import com.study.common.exception.ErrorCode;

public class S3UploadException extends UploadException{
    public S3UploadException(String fileName) {
        super(fileName, ErrorCode.FAIL_S3_UPLOAD);
    }
}

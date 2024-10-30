package com.study.common.exception.comment;


import com.study.common.exception.ErrorCode;

public class NotExistCommentException extends CommentException{
    public NotExistCommentException() {
        super(ErrorCode.NOT_EXIST_COMMENT);
    }
}

package com.study.common.exception.liked;

import com.study.common.exception.ErrorCode;

public class PostDeleteLikeException extends LikedException{
    public PostDeleteLikeException(ErrorCode errorCode) {
            super(ErrorCode.POST_DELETE_LIKE_FAIL_EXCEPTION);
        }
    }

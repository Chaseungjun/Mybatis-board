package com.study.common.exception.liked;

import com.study.common.exception.ErrorCode;

public class PostLikedException extends LikedException{

    public PostLikedException(ErrorCode errorCode) {
        super(ErrorCode.POST_LIKE_FAIL_EXCEPTION);
    }

}

package com.study.domain.liked.entity;

import lombok.Getter;

@Getter
public class Liked {

    private int id;
    private String userId;
    private long postId;

    public Liked(String userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}

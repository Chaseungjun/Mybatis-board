package com.study.domain.notification.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * 알림 엔티티 클래스.
 * 이 클래스는 사용자에게 전송될 알림의 세부 정보를 정의합니다.
 * 알림의 내용, 관련 URL, 알림 타입, 수신자 ID 등을 포함합니다.
 */
@Getter
public class Notification {

    private Long id;

    // 알림 내용
    private String content;

    // 관련 URL
    private String url;

    // 읽음 여부 (알림을 읽었는지 여부)
    private Boolean isRead;

    // 알림 타입 (알림의 유형을 나타내는 ENUM)
    private NotificationType notificationType;

    // 수신자 ID (알림을 받을 회원의 ID)
    private String userId;

    /**
     * 알림 객체의 생성자.
     *
     * @param userId 수신자 ID (회원 ID)
     * @param notificationType 알림 타입 (예: COMMENT 등)
     * @param content 알림 내용
     * @param url 관련 URL
     * @param isRead 알림 읽음 여부
     */
    @Builder
    public Notification(String userId, NotificationType notificationType, String content, String url, Boolean isRead) {
        this.userId = userId;
        this.notificationType = notificationType;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }

    /**
     * 알림의 타입을 정의하는 열거형.
     * 알림의 종류를 구분하는데 사용됩니다.
     */
    public enum NotificationType {
        /**
         * 댓글 알림
         */
        COMMENT
    }

    public void setIsReadToTrue(Boolean isRead) {
        this.isRead = isRead;
    }
}

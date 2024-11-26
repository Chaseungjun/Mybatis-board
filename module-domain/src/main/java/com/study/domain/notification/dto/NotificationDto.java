package com.study.domain.notification.dto;

import com.study.domain.notification.entity.Notification;
import lombok.Builder;

/**
 * 알림 관련 DTO(Data Transfer Object) 클래스입니다.
 * 이 클래스는 알림 데이터를 전송할 때 사용되며, {@link Notification} 엔티티 객체를 {@link NotificationResponseDto} 형태로 변환하는 데 사용됩니다.
 */
public class NotificationDto {

    /**
     * 알림 응답 DTO 클래스.
     * 클라이언트에게 전송할 알림 정보를 담고 있습니다.
     */
    @Builder
    public record NotificationResponseDto(
            Long id,

            // 알림 내용
            String content,

            // 관련 URL
            String url,

            // 읽음 여부 (알림을 읽었는지 여부)
            Boolean isRead,

            // 알림 타입 (ENUM으로 정의된 알림 유형)
            Notification.NotificationType notificationType,

            // 수신자 ID (회원 ID)
            String userId
    ) {

        /**
         * {@link Notification} 엔티티 객체를 {@link NotificationResponseDto}로 변환하는 메서드.
         *
         * @param notification 변환할 {@link Notification} 엔티티 객체
         * @return 변환된 {@link NotificationResponseDto} 객체
         */
        public static NotificationResponseDto of(Notification notification) {
            return NotificationResponseDto.builder()
                    .id(notification.getId())
                    .content(notification.getContent())
                    .url(notification.getUrl())
                    .isRead(notification.getIsRead())
                    .notificationType(notification.getNotificationType())
                    .userId(notification.getUserId())
                    .build();
        }
    }
}

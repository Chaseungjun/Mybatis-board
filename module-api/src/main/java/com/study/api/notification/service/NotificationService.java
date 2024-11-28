package com.study.api.notification.service;

import com.study.domain.mapper.notification.EmitterRepository;
import com.study.domain.mapper.notification.NotificationCommandMapper;
import com.study.domain.mapper.notification.NotificationQueryMapper;
import com.study.domain.notification.dto.NotificationDto;
import com.study.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

/**
 * 알림 서비스 클래스는 사용자에게 실시간 알림을 전송하는 기능을 제공합니다.
 * <p>
 * 1. 사용자가 구독할 때 SseEmitter를 생성하여 사용자에게 알림을 전송할 준비를 합니다.
 * 2. 알림을 전송하고, 서버가 클라이언트에게 실시간 이벤트를 전송할 수 있게 합니다.
 * 3. 미수신한 이벤트를 재전송하여 이벤트 유실을 방지합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationCommandMapper notificationCommandMapper;
    private final NotificationQueryMapper notificationQueryMapper;

    private final long TIMEOUT = 60 * 1000; // 60초 타임아웃 설정

    /**
     * 사용자가 구독을 요청할 때 호출되어 SseEmitter를 생성하고, 해당 사용자의 알림을 전송할 준비를 합니다.
     * <p>
     * 1. 구독 시 더미 이벤트를 전송하여 연결을 확인합니다.
     * 2. 클라이언트가 미수신한 이벤트가 있을 경우, 그 데이터를 전송하여 이벤트 유실을 방지합니다.
     * </p>
     *
     * @param userId       사용자의 ID
     * @param lastEventId  마지막으로 수신한 이벤트 ID
     * @return SseEmitter  사용자의 알림을 실시간으로 전송할 수 있는 SseEmitter 객체
     */
    public SseEmitter subscribe(String userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter sseEmitter = createEmitter();
        SseEmitter emitter = emitterRepository.save(emitterId, sseEmitter);

        emitter.onCompletion(() -> {
            emitterRepository.deleteByEmitterId(emitterId);
            log.info("server sent event removed in emitter cache: emitterId={}, userId={}", emitterId, userId);
        });
        emitter.onTimeout(() -> {
            emitterRepository.deleteByEmitterId(emitterId);
            log.info("server sent event timed out : emitterId={}, userId={}", emitterId, userId);
        });

        // 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 미수신한 이벤트가 있을 경우 재전송
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    /**
     * 알림을 읽음 상태로 업데이트합니다.
     *
     * @param notificationId 알림 ID
     */
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationQueryMapper.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다. ID: " + notificationId));

        if (notification.getIsRead() == false) {
            notification.setIsReadToTrue(true);
            notificationCommandMapper.updateNotificationReadStatus(notificationId, true);
        }
    }

    /**
     * 사용자의 알림을 전송하는 메서드입니다.
     * <p>
     * 1. 알림 객체를 생성하고 DB에 저장합니다.
     * 2. 저장된 알림을 모든 연결된 클라이언트에 전송합니다.
     * </p>
     *
     * @param userId        알림을 받을 사용자 ID
     * @param notificationType 알림의 유형
     * @param content       알림의 내용
     * @param url           알림을 클릭 시 이동할 URL
     */
    @Async
    public void send(String userId, Notification.NotificationType notificationType, String content, String url) {
        Notification notification = createNotification(userId, notificationType, content, url);
        notificationCommandMapper.save(notification);
        String eventId = userId + "_" + System.currentTimeMillis(); // 이벤트 ID 생성
        Map<String, SseEmitter> userEmitters = emitterRepository.findAllEmitterStartWithByMemberId(userId); // 사용자에 대한 모든 Emitter 조회

        userEmitters.forEach((emitterId, emitter) -> {
            emitterRepository.saveEventCache(emitterId, notification);
            sendNotification(emitter, eventId, emitterId, NotificationDto.NotificationResponseDto.of(notification));
        });
    }

    /**
     * SseEmitter를 통해 클라이언트에게 알림을 전송합니다.
     *
     * @param emitter   알림을 전송할 SseEmitter 객체
     * @param eventId   전송할 이벤트의 ID
     * @param emitterId Emitter의 ID
     * @param data      전송할 데이터
     */
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("SseNotification")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteByEmitterId(emitterId);
        }
    }

    /**
     * 새 알림을 생성하여 반환합니다.
     *
     * @param userId        알림을 받을 사용자 ID
     * @param notificationType 알림의 유형
     * @param content       알림의 내용
     * @param url           알림을 클릭 시 이동할 URL
     * @return Notification 생성된 알림 객체
     */
    private Notification createNotification(String userId, Notification.NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .userId(userId)
                .notificationType(notificationType)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }

    /**
     * 미수신 이벤트가 있는지 확인합니다.
     *
     * @param lastEventId 마지막으로 수신한 이벤트의 ID
     * @return 미수신 이벤트가 있을 경우 true, 없으면 false
     */
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    /**
     * 미수신한 데이터를 전송하여 이벤트 유실을 방지합니다.
     *
     * @param lastEventId  마지막으로 수신한 이벤트 ID
     * @param userEmail    사용자의 이메일
     * @param emitterId    Emitter의 ID
     * @param emitter      알림을 전송할 SseEmitter 객체
     */
    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    /**
     * 사용자 ID와 현재 시간을 결합하여 고유한 ID를 생성합니다.
     *
     * @param userId 사용자 ID
     * @return 고유한 ID
     */
    private String makeTimeIncludeId(String userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    /**
     * 새로운 SseEmitter 객체를 생성합니다.
     *
     * @return SseEmitter 객체
     */
    private SseEmitter createEmitter() {
        return new SseEmitter(TIMEOUT);
    }
}

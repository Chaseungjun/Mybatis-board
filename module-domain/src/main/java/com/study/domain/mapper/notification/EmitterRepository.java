package com.study.domain.mapper.notification;

import com.study.domain.notification.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    void saveEventCache(String emitterId, Notification notification);
    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String userId);
    Map<String, Object> findAllEventCacheStartWithByMemberId(String userId);
    void deleteByEmitterId(String emitter);
    void deleteAllEmitterStartWithId(String emitter);
    void deleteAllEventCacheStartWithId(String emitter);
}

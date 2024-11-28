package com.study.domain.mapper.notification;

import com.study.domain.notification.entity.Notification;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link EmitterRepository}의 구현체로, 클라이언트의 SSE 연결과 이벤트 캐시를 관리하는 저장소입니다.
 *
 * 이 클래스는 SSE 연결을 관리하고, 특정 사용자의 SSE 연결과 이벤트 캐시를 저장하거나 삭제하는 역할을 합니다.
 */
@Repository
public class EmitterRepositoryImpl implements EmitterRepository {

    // SSE 연결을 저장하는 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 이벤트 캐시를 저장하는 맵
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    /**
     * 주어진 emitterId와 SSE emitter를 저장합니다.
     *
     * @param emitterId 저장할 emitter의 ID
     * @param sseEmitter 저장할 SSE emitter
     * @return 저장된 SSE emitter 객체
     */
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    /**
     * 주어진 이벤트 캐시 ID와 알림을 저장합니다.
     *
     * @param eventCacheId 저장할 이벤트 캐시의 ID
     * @param notification 저장할 알림 객체
     */
    @Override
    public void saveEventCache(String eventCacheId, Notification notification) {
        eventCache.put(eventCacheId, notification);
    }

    /**
     * 특정 회원 ID와 관련된 모든 emitter를 반환합니다.
     *
     * @param memberId 회원 ID
     * @return 해당 회원과 관련된 모든 SSE emitter를 담은 맵
     */
    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 특정 회원 ID와 관련된 모든 이벤트 캐시를 반환합니다.
     *
     * @param memberId 회원 ID
     * @return 해당 회원과 관련된 모든 이벤트 캐시를 담은 맵
     */
    @Override
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 주어진 emitter ID에 해당하는 emitter를 삭제합니다.
     *
     * @param id 삭제할 emitter의 ID
     */
    @Override
    public void deleteByEmitterId(String id) {
        emitters.remove(id);
    }

    /**
     * 특정 회원 ID와 관련된 모든 SSE emitter를 삭제합니다.
     *
     * @param memberId 회원 ID
     */
    @Override
    public void deleteAllEmitterStartWithId(String memberId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    /**
     * 특정 회원 ID와 관련된 모든 이벤트 캐시를 삭제합니다.
     *
     * @param memberId 회원 ID
     */
    @Override
    public void deleteAllEventCacheStartWithId(String memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
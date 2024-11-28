package com.study.domain.mapper.notification;

import com.study.domain.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * NotificationCommandMapper 는 알림 관련 데이터베이스 명령 작업을 수행하는 MyBatis 매퍼 인터페이스입니다.
 */
@Mapper
public interface NotificationCommandMapper {

    /**
     * 주어진 알림 정보를 데이터베이스에 저장합니다.
     *
     * @param notification 저장할 {@link Notification} 객체
     * @return 저장된 알림의 행 수
     */
    int save(Notification notification);

    /**
     * 클라이언트가 알람을 읽은 경우, Notification 객체의 알람상태를 읽음 상태로 변경합니다.
     *
     * @param id notification 객체의 id
     * @param isRead 읽음 여부
     */
    void updateNotificationReadStatus(@Param("id") Long id, @Param("isRead") boolean isRead);
}
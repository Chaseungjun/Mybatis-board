package com.study.domain.mapper.notification;

import com.study.domain.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * NotificationQueryMapper 는 알림 관련 데이터베이스 쿼리 작업을 수행하는 MyBatis 매퍼 인터페이스입니다.
 * 알림 정보를 데이터베이스에서 조회하는 기능을 제공합니다.
 */
@Mapper
public interface NotificationQueryMapper {

    /**
     * 알림 정보를 데이터베이스에서 조회합니다.
     *
     * @param id 알람 엔티티의 id
     * @return 알람 엔티티
     */
    Optional<Notification> findById(Long id);
}

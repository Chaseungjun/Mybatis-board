package com.study.api.notification.controller;

import com.study.api.config.annotation.AuthenticatedUserId;
import com.study.api.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            method = "GET",
            summary = "알람을 구독한다.",
            description = "실제 클라이언트로부터 오는 알림 구독 요청을 받는다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 구독 성공",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE)),
            @ApiResponse(responseCode = "400", description = "알림 구독에 실패했습니다.", content = @Content)
    })
    @GetMapping(value = "/subscribe" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @AuthenticatedUserId String userId,
            @RequestHeader(value="Last-Event-ID", required = false, defaultValue = "") String lastEventId // SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id 값을 의미
    ) {
        return notificationService.subscribe(userId, lastEventId);
    }


    @Operation(
            method = "PATCH",
            summary = "알람을 읽음 상태로 수정한다.",
            description = "클라이언트가 알람을 읽은 경우, 알람을 읽음 상태로 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽음 상태로 수정 성공",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE)),
            @ApiResponse(responseCode = "400", description = "읽음 상태로 수정 실패", content = @Content)
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

}

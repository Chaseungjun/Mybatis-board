package com.study.common.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 애플리케이션의 에러 코드를 정의하는 열거형 클래스입니다.
 */
@Getter
public enum ErrorCode {

    // 회원가입 관련 에러 코드
    /**
     * 중복된 사용자 ID 에러
     */
    DUPLICATE_USERID(409, "SU001", "존재하는 아이디입니다."),

    /**
     * 중복된 닉네임 에러
     */
    DUPLICATE_NICKNAME(409, "SU002", "존재하는 닉네임입니다."),

    /**
     * 중복된 이메일 에러
     */
    DUPLICATE_EMAIL(409, "SU003", "이미 가입된 이메일입니다."),

    /**
     * 이메일 인증 실패 에러
     */
    FAIL_AUTHENTICATION_EMAIL(401, "SU005", "이메일 인증에 실패했습니다."),

    /**
     * 이메일 전송 실패 에러
     */
    FAIL_SEND_EMAIL(401, "SU008", "이메일 전송에 실패했습니다."),

    /**
     * 비밀번호 불일치 에러
     */
    NOT_MATCH_PASSWORD(401, "SU006", "비밀번호가 일치하지 않습니다."),

    /**
     * S3 업로드 실패 에러
     */
    FAIL_S3_UPLOAD(500, "SU007", "S3 업로드에 실패했습니다."),

    /**
     * 사용자를 찾을 수 없음 에러
     */
    NOT_FOUND_USER(404, "SU008", "유저가 존재하지 않습니다."),

    /**
     * 로그인이 필요함 에러
     */
    NOT_SIGNED_IN(401, "USER_NOT_SIGNED_IN", "로그인이 필요합니다."),

    /**
     * 인증 진입점 처리 에러
     */
    HANDLE_AUTHENTICATION_ENTRYPOINT(401, "C001", "인증과정에 문제가 발생했습니다."),

    /**
     * JSON 처리 에러
     */
    JSON_PROCESSING_EXCEPTION(500, "C002", "JSON 처리 중 오류가 발생했습니다."),

    /**
     * 권한 없음 에러
     */
    DONT_HAVE_AUTHENTICATION(403, "FORBIDDEN_ACCESS", "권한이 없습니다."),

    /**
     * 게시글 부존재 에러
     */
    NOT_EXIST_POST(404, "NOT_EXIST_POST", "게시글이 존재하지 않습니다."),

    /**
     * 댓글 부존재 에러
     */
    NOT_EXIST_COMMENT(404, "NOT_EXIST_COMMENT", "댓글이 존재하지 않습니다."),

    /**
     * 좋아요 에러
     */
    POST_LIKE_FAIL_EXCEPTION(400, "BOARD_LIKE_FAIL_EXCEPTION", "좋아요 추가에 실패했습니다."),

    /**
     * 좋아요 취소 에러
     */
    POST_DELETE_LIKE_FAIL_EXCEPTION(400, "BOARD_DELETE_LIKE_FAIL_EXCEPTION", "좋아요 취소에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
    private final LocalDateTime localDateTime;
    private final String requestURI;

    /**
     * 주어진 상태 코드, 에러 코드 및 메시지로 새로운 ErrorCode를 생성합니다.
     *
     * @param status  HTTP 상태 코드
     * @param code    에러 코드
     * @param message 에러 메시지
     */
    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.localDateTime = LocalDateTime.now();
        this.requestURI = getRequestURI();
    }
}

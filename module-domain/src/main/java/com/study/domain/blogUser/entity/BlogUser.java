package com.study.domain.blogUser.entity;

import com.study.domain.blogUser.dto.BlogUserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * 블로그 사용자(BlogUser) 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogUser {

    /**
     * 블로그 사용자 ID (자동 생성).
     */
    private long id;

    /**
     * 사용자의 ID입니다.
     */
    private String userId;

    /**
     * 사용자의 비밀번호입니다.
     */
    private String password;

    /**
     * 사용자의 이메일입니다.
     */
    private String email;

    /**
     * 사용자의 이름입니다.
     */
    private String userName;

    /**
     * 사용자의 닉네임입니다.
     */
    private String nickName;

    /**
     * 사용자의 전화번호입니다.
     */
    private String phoneNumber;

    /**
     * 사용자의 프로필 이미지 URL입니다.
     */
    private String profileImageUrl;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    /**
     * BlogUser 인스턴스를 생성하는 생성자입니다.
     *
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param email 이메일
     * @param userName 이름
     * @param nickName 닉네임
     * @param phoneNumber 전화번호
     * @param profileImageUrl 프로필 이미지 URL
     */
    @Builder
    public BlogUser(String userId, String password, String email, String userName, String nickName, String phoneNumber, String profileImageUrl) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.created_at = LocalDateTime.now();
        this.updated_at = null;
        this.deleted_at = null;
    }

    /**
     * BlogUserRegistrationRequest DTO와 비밀번호 인코더를 사용하여 새로운 BlogUser를 생성합니다.
     *
     * @param registrationRequest 회원가입 요청 DTO
     * @param encoder 비밀번호 인코더(암호화)
     * @param profileImageUrl 프로필 이미지 URL
     * @return 생성된 BlogUser 인스턴스
     */
    public static BlogUser of(BlogUserDto.IndividualSignupRequestDto registrationRequest, PasswordEncoder encoder, String profileImageUrl) {
        return BlogUser.builder()
                .userId(registrationRequest.userId())
                .password(encoder.encode(registrationRequest.password()))
                .userName(registrationRequest.userName())
                .email(registrationRequest.email())
                .phoneNumber(registrationRequest.phoneNumber())
                .profileImageUrl(profileImageUrl)
                .nickName(registrationRequest.nickname())
                .build();
    }
}

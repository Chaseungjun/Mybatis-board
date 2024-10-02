package com.study.domain.blogUser.dto;

import com.study.domain.blogUser.entity.BlogUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * BlogUser에 대한 데이터 전송 객체(DTO)입니다.
 */
public class BlogUserDto {

    /**
     * BlogUser 회원가입 요청을 위한 DTO입니다.
     */
    @Builder
    public record IndividualSignupRequestDto(
            /**
             * 사용자의 ID입니다.
             */
            @NotBlank
            @Pattern(regexp = "^[a-z0-9]{5,15}$", message = "아이디는 5자 이상 15자 이하의 영어 소문자 또는 영어 소문자 + 숫자여야 합니다.")
            @Schema(description = "사용자의 ID입니다. 5자 이상 15자 이하의 영어 소문자 또는 영어 소문자 + 숫자", example = "user123")
            String userId,

            /**
             * 사용자의 비밀번호입니다.
             */
            @NotBlank
            @Pattern(
                    regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                    message = "비밀번호는 영문자, 숫자, 특수문자를 혼용하여 8자 이상이어야 하며, 특정 특수문자는 사용할 수 없습니다."
            )
            @Schema(description = "사용자의 비밀번호입니다. 영문자, 숫자, 특수문자를 혼용하여 8자 이상", example = "Password1!")
            String password,

            /**
             * 비밀번호 확인 값입니다.
             */
            @NotBlank
            @Schema(description = "비밀번호 확인 값입니다.", example = "Password1!")
            String validPassword,

            /**
             * 사용자의 이메일입니다.
             */
            @NotBlank
            @Email
            @Schema(description = "사용자의 이메일입니다.", example = "chas369@naver.com")
            String email,

            /**
             * 사용자의 이름입니다.
             */
            @NotBlank
            @Pattern(regexp = "^[가-힣]{2,}$", message = "이름은 2자 이상의 한글이어야 합니다.")
            @Schema(description = "사용자의 이름입니다. 2자 이상의 한글", example = "홍길동")
            String userName,

            /**
             * 사용자의 닉네임입니다.
             */
            @NotBlank
            @Pattern(regexp = "^[a-zA-Z가-힣]{2,}$", message = "닉네임은 2글자 이상의 한글 또는 영문이어야 합니다.")
            @Schema(description = "사용자의 닉네임입니다. 2글자 이상의 한글 또는 영문", example = "nickname")
            String nickname,

            /**
             * 사용자의 전화번호입니다.
             */
            @NotBlank
            @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 -를 제외하고 10자리 또는 11자리 숫자여야 합니다.")
            @Schema(description = "사용자의 전화번호입니다. -를 제외한 숫자 10자리 또는 11자리", example = "01012345678")
            String phoneNumber
    ) {}

    /**
     * BlogUser 응답을 위한 DTO입니다.
     */
    @Builder
    public record BlogUserResponse(
            /**
             * 사용자의 ID입니다.
             */
            @Schema(description = "사용자의 ID입니다.", example = "user123")
            String userId,

            /**
             * 사용자의 이메일입니다.
             */
            @Schema(description = "사용자의 이메일입니다.", example = "chas369@naver.com")
            String email,

            /**
             * 사용자의 이름입니다.
             */
            @Schema(description = "사용자의 이름입니다.", example = "홍길동")
            String userName,

            /**
             * 사용자의 닉네임입니다.
             */
            @Schema(description = "사용자의 닉네임입니다.", example = "nickname")
            String nickname,

            /**
             * 사용자의 전화번호입니다.
             */
            @Schema(description = "사용자의 전화번호입니다.", example = "01012345678")
            String phoneNumber,

            /**
             * 사용자의 프로필 이미지 URL입니다.
             */
            @Schema(description = "사용자의 프로필 이미지 URL입니다.", example = "https://example.com/profile.jpg")
            String profileImageUrl
    ) {
        /**
         * BlogUser 엔티티로부터 BlogUserResponse를 생성합니다.
         *
         * @param blogUser BlogUser 엔티티
         * @return BlogUserResponse 인스턴스
         */
        public static BlogUserResponse from(BlogUser blogUser) {
            return new BlogUserResponse(
                    blogUser.getUserId(),
                    blogUser.getEmail(),
                    blogUser.getUserName(),
                    blogUser.getNickName(),
                    blogUser.getPhoneNumber(),
                    blogUser.getProfileImageUrl()
            );
        }
    }

}

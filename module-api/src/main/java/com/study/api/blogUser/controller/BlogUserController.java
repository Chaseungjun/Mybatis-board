package com.study.api.blogUser.controller;

import com.study.api.blog.service.BlogWithdrawService;
import com.study.api.blogUser.service.*;
import com.study.api.config.annotation.AuthenticatedUserId;
import com.study.common.exception.authentication.EmailAuthenticationException;
import com.study.domain.blogUser.dto.BlogUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * BlogUser 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class BlogUserController {

    private final BlogUserSignUpService blogUserService;
    private final SmtpEmailService smtpEmailService;
    private final BlogUserSignInService signInService;
    private final BlogUserSignOutService signOutService;
    private final BlogUserWithdrawService blogUserWithdrawService;
    private final BlogWithdrawService blogWithdrawService;

    /**
     * 사용자를 등록합니다.
     *
     * @param request 회원가입 요청 정보
     * @param file 프로필 이미지 파일 (선택 사항)
     * @return 등록된 사용자의 응답 정보
     */
    @Operation(
            method = "POST",
            summary = "회원가입",
            description = "아이디, 비밀번호 등을 입력하고, 프로필 사진을 업로드한 후 이메일 인증을 거쳐 회원가입합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "존재하는 아이디입니다."),
            @ApiResponse(responseCode = "409", description = "존재하는 닉네임입니다."),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일입니다."),
            @ApiResponse(responseCode = "409", description = "존재하는 아이디입니다."),
            @ApiResponse(responseCode = "401", description = "이메일 인증에 실패했습니다."),
            @ApiResponse(responseCode = "401", description = "이메일 전송에 실패했습니다."),
            @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
            @ApiResponse(responseCode = "500", description = "S3 업로드에 실패했습니다.")
    })
    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<BlogUserDto.BlogUserResponse> register(
            @RequestPart @Valid BlogUserDto.IndividualSignupRequestDto request,
            @RequestPart(required = false) MultipartFile file
    ) {
        boolean isEmailVerified = smtpEmailService.isEmailVerified(request.email());
        if (!isEmailVerified) {
            throw new EmailAuthenticationException(request.email());
        }

        BlogUserDto.BlogUserResponse blogUserResponse = blogUserService.individualGeneralSignUp(request, file);
        return ResponseEntity.ok(blogUserResponse);
    }

    /**
     * 사용자의 회원가입을 위한 인증 코드를 이메일로 전송합니다.
     *
     * @param email 인증 코드를 받을 이메일 주소
     * @return 이메일 전송 성공 여부를 나타내는 ResponseEntity (true: 성공, false: 실패)
     */
    @Operation(
            method = "POST",
            summary = "인증코드 이메일 전송",
            description = "입력한 이메일로 숫자로 이루어진 인증코드를 전송합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증코드 전송 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 전송에 실패했습니다.")
    })
    @PostMapping("/send-verificationCode")
    public ResponseEntity<Boolean> sendVerificationCode(@RequestParam String email) {
        boolean result = smtpEmailService.emailForSignup(email);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자가 입력한 인증 코드를 검증합니다.
     *
     * @param email 인증할 이메일 주소
     * @param code 사용자가 입력한 인증 코드
     * @return 인증 코드 검증 결과를 나타내는 ResponseEntity (true: 성공, false: 실패)
     */
    @Operation(
            method = "POST",
            summary = "인증코드 일치여부 확인",
            description = "입력한 이메일로 전송된 인증번호와 입력한 인증번호가 일치하는지 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증코드 일치 확인 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 인증에 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/send-verificationCode/check")
    public ResponseEntity<Boolean> checkVerificationCode(@RequestParam String email, @RequestParam String code) {
        boolean result = smtpEmailService.verifyCode(email, code);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자 로그인 요청을 처리합니다.
     *
     * @param request 로그인 요청 정보
     * @param response HTTP 응답 객체
     * @return 로그인 성공 시 빈 응답
     */
    @Operation(
            method = "POST",
            summary = "로그인",
            description = "사용자의 아이디와 비밀번호를 입력하여 로그인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않습니다.")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> signIn(
            @RequestBody @Valid BlogUserDto.BlogUserSignInRequestDto request,
            HttpServletResponse response
    ) {
        signInService.signIn(response, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 로그아웃 요청을 처리합니다.
     *
     * @param response HTTP 응답 객체
     * @param userId 현재 로그인 중인 사용자 ID
     * @return HTTP 상태 코드 204 (No Content) 로그아웃 성공
     */
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인 중인 사용자가 로그아웃을 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(HttpServletResponse response, @AuthenticatedUserId String userId) {
        signOutService.signOut(response, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원탈퇴 요청을 처리합니다.
     *
     * @param userId 현재 로그인 중인 사용자 ID
     * @return HTTP 상태 코드 204 (No Content) 회원탈퇴 성공
     */
    @Operation(
            summary = "회원탈퇴",
            description = "현재 로그인 중인 사용자가 자신의 계정을 탈퇴합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청 또는 인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticatedUserId String userId){
        blogUserWithdrawService.withdrawBlogUser(userId);
        blogWithdrawService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }

}

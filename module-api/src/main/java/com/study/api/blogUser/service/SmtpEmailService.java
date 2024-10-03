package com.study.api.blogUser.service;

import com.study.common.exception.ErrorCode;
import com.study.common.exception.authentication.EmailAuthenticationException;
import com.study.common.exception.duplicate.EmailDuplicateException;
import com.study.common.exception.email.EmailSendingException;

import com.study.domain.mapper.bloguser.BlogUserQueryMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * SMTP 이메일 서비스를 제공하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService {

    private final JavaMailSender javaMailSender;
    private final BlogUserQueryMapper blogUserQueryMapper;
    private final Map<String, String> verificationCodes = new HashMap<>();
    private Map<String, Boolean> emailVerificationStatus  = new HashMap<>();


    /**
     * 회원가입을 위한 인증 이메일을 발송합니다.
     *
     * @param email 인증 이메일을 받을 이메일 주소
     * @throws MessagingException      이메일 발송 중 예외가 발생할 경우
     * @throws EmailDuplicateException 이메일이 이미 존재할 경우
     */
    public boolean emailForSignup(String email) {

        if (blogUserQueryMapper.existsBlogUserByEmail(email)) {
            throw new EmailDuplicateException("이미 존재하는 이메일입니다.");
        }
        String verificationCode = generateVerificationCode(email);
        sendVerificationEmail(email, verificationCode);
        return true;
    }

    /**
     * 인증번호를 생성합니다.
     *
     * @param email 인증번호를 받을 이메일 주소
     * @return 생성된 인증번호
     */
    private String generateVerificationCode(String email) {
        Random random = new Random();
        String code = String.valueOf(100000 + random.nextInt(900000));
        verificationCodes.put(email, code);
        return code;
    }

    /**
     * 인증번호를 검증합니다.
     *
     * @param email 인증할 이메일 주소
     * @param code  검증할 인증번호
     * @return 인증번호가 유효한지 여부
     */
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);

        if (storedCode == null || !storedCode.equals(code)) {
            throw new EmailAuthenticationException(email);
        }
        emailVerificationStatus.put(email, true);
        clearCode(email);
        return true;
    }

    /**
     * 이메일의 인증번호를 삭제합니다.
     *
     * @param email 인증번호를 삭제할 이메일 주소
     */
    public void clearCode(String email) {
        verificationCodes.remove(email);
    }

    /**
     * 인증 이메일을 생성 및 발송합니다.
     *
     * @param email            인증 이메일을 받을 이메일 주소
     * @param verificationCode 발송할 인증번호
     * @throws MessagingException 이메일 발송 중 예외가 발생할 경우
     */
    private void sendVerificationEmail(String email, String verificationCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom("chas369@naver.com");
            helper.setTo(email);
            helper.setSubject("회원가입 인증 이메일");
            helper.setText("회원가입 인증번호는 " + verificationCode + " 입니다.");
            log.info("email = {}, verificationCode = {}", email, verificationCode);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailSendingException(email, ErrorCode.FAIL_SEND_EMAIL);
        }
    }

    /**
     * 주어진 이메일 주소에 대한 인증 여부를 확인합니다.
     *
     * @param email 인증 여부를 확인할 이메일 주소
     * @return 이메일이 인증된 경우 true, 그렇지 않은 경우 false
     */
    public boolean isEmailVerified(String email){
        Boolean isVerified = emailVerificationStatus.get(email);
        return isVerified != null && isVerified;
    }
}

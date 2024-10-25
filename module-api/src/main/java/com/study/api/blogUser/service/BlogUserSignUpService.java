package com.study.api.blogUser.service;

import com.study.api.fileUpload.service.S3Uploader;
import com.study.common.exception.duplicate.EmailDuplicateException;
import com.study.common.exception.duplicate.NickNameDuplicateException;
import com.study.common.exception.duplicate.UserIdDuplicateException;
import com.study.common.exception.match.ValidPasswordException;
import com.study.domain.blog.entity.Blog;
import com.study.domain.blogUser.dto.BlogUserDto;
import com.study.domain.blogUser.entity.BlogUser;
import com.study.domain.mapper.blog.BlogCommandMapper;
import com.study.domain.mapper.bloguser.BlogUserCommandMapper;
import com.study.domain.mapper.bloguser.BlogUserQueryMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 블로그 사용자 관련 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class BlogUserSignUpService {


    private final BlogCommandMapper blogCommandMapper;
    private final BlogUserQueryMapper blogUserQueryMapper;
    private final BlogUserCommandMapper blogUserCommandMapper;
    private final PasswordEncoder encoder;
    private final S3Uploader s3Uploader;

    @Value("${default.profile.image.url}")  // 기본 프로필 이미지 URL 설정
    private String defaultProfileImageUrl;

    /**
     * 사용자를 등록합니다.
     *
     * @param request 사용자 등록 요청 정보
     * @param file 프로필 이미지 파일
     * @return 등록된 사용자의 응답 DTO
     */
    @Transactional
    public BlogUserDto.BlogUserResponse individualGeneralSignUp(
            BlogUserDto.IndividualSignupRequestDto request,
            MultipartFile file
    ) {

        validateRequest(request);
        String profileImageUrl;

        if (file != null && !file.isEmpty()) {
            profileImageUrl = s3Uploader.upload(file, "profile-image");
        } else {
            profileImageUrl = defaultProfileImageUrl; // 기본 이미지 URL
        }

        BlogUser blogUser = BlogUser.of(request, encoder, profileImageUrl);
        blogUserCommandMapper.save(blogUser);


        Blog blog = Blog.from(blogUser.getUserId());
        blogCommandMapper.save(blog);

        return BlogUserDto.BlogUserResponse.from(blogUser);
    }

    /**
     * 사용자 등록 요청을 검증합니다.
     *
     * @param request 사용자 등록 요청 정보
     */
    private void validateRequest(BlogUserDto.IndividualSignupRequestDto request) {
        checkDuplicateUserId(request.userId());
        checkDuplicateNickName(request.nickname());
        checkDuplicateEmail(request.email());
        validPassword(request);
    }

    /**
     * 이메일 중복을 확인합니다.
     *
     * @param email 확인할 이메일
     * @throws EmailDuplicateException 이메일이 중복될 경우 발생
     */
    private void checkDuplicateEmail(String email) {
        if (blogUserQueryMapper.existsBlogUserByEmail(email))
            throw new EmailDuplicateException(email);
    }

    /**
     * 사용자 ID 중복을 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @throws UserIdDuplicateException 사용자 ID가 중복될 경우 발생
     */
    private void checkDuplicateUserId(String userId) {
        if (blogUserQueryMapper.existsByUserId(userId))
            throw new UserIdDuplicateException(userId);
    }

    /**
     * 닉네임 중복을 확인합니다.
     *
     * @param nickName 확인할 닉네임
     * @throws NickNameDuplicateException 닉네임이 중복될 경우 발생
     */
    private void checkDuplicateNickName(String nickName) {
        if (blogUserQueryMapper.existsByNickname(nickName))
            throw new NickNameDuplicateException(nickName);
    }

    /**
     * 비밀번호 유효성을 확인합니다.
     *
     * @param request 사용자 등록 요청 정보
     * @throws ValidPasswordException 비밀번호와 비밀번호 재입력이 일치하지 않을 경우 발생
     */
    private void validPassword(BlogUserDto.IndividualSignupRequestDto request) {
        if (!request.password().equals(request.validPassword()))
            throw new ValidPasswordException(request.validPassword());
    }
}

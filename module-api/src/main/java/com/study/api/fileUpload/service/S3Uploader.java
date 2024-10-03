package com.study.api.fileUpload.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.study.common.exception.upload.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/**
 * AWS S3에 파일을 업로드하는 서비스 클래스입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일을 AWS S3 버킷에 업로드합니다.
     *
     * @param file    업로드할 파일
     * @param dirName S3 버킷 내의 디렉토리 이름
     * @return 업로드된 파일의 URL
     * @throws S3UploadException 파일 업로드 중 오류가 발생한 경우
     */
    public String upload(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = createObjectMetadata(file);

        try {
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata); // 버킷에 업로드
        } catch (IOException e) {
            log.error("S3 업로드 실패", e);
            throw new S3UploadException(fileName);
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * 파일의 메타데이터를 생성합니다.
     *
     * @param file 메타데이터를 생성할 파일
     * @return 생성된 파일 메타데이터
     */
    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }
}
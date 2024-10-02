package com.study.api.fileUpload.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS S3 스토리지 구성을 위한 설정 클래스입니다.
 */
@Getter
@Configuration
public class StorageConfig {

    /**
     * AWS 계정의 액세스 키입니다.
     */
    @Value(("${cloud.aws.credentials.access-key}"))
    private String accessKey;

    /**
     * AWS 계정의 시크릿 키입니다.
     */
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    /**
     * 사용할 AWS 지역 정보입니다.
     */
    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * Amazon S3 클라이언트 빈을 생성하여 반환합니다.
     *
     * @return AmazonS3Client 인스턴스
     */
    @Bean
    public AmazonS3Client amazonS3Client(){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}

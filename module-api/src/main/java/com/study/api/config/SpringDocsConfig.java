package com.study.api.config;


import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {

    @Bean
    public OpenAPI openAPI() {  // OpenAPI 객체를 생성하고, API 문서의 정보를 설정
        return new OpenAPI()
                .info(getApiInfo())
                .paths(getPaths());
    }

    private Info getApiInfo() {  // API 문서의 기본 정보를 설정
        return new Info()
                .title("API 문서")
                .version("v1")
                .description("API 문서 설명")
                .contact(new Contact()
                        .email("chas369@naver.com"));
    }



    private Paths getPaths() {
        Paths paths = new Paths();

        paths.addPathItem("/users/send-verificationCode", new PathItem()
                .post(new Operation().security(null)));

        paths.addPathItem("/users/send-verificationCode/check", new PathItem()
                .post(new Operation().security(null)));

        paths.addPathItem("/users", new PathItem()
                .post(new Operation().security(null)));

        return paths;
    }
}


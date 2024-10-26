package com.study.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = {
        "com.study.common", "com.study.domain"
})
public class ModuleDomainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleDomainApplication.class, args);
    }

}

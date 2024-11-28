package com.study.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3); // 기본적으로 유지되는 스레드 수
        taskExecutor.setMaxPoolSize(10); // 최대 허용되는 스레드 수
        taskExecutor.setQueueCapacity(50); // 작업 대기열 크기
        taskExecutor.setThreadNamePrefix("async-thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }

}

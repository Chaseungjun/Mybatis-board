package com.study.domain.redis.config;

import com.study.domain.post.dto.PostDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis 관련 설정을 구성하는 클래스입니다.
 */
@Configuration
public class RedisConfig {


    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    /**
     * RedisConnectionFactory를 생성합니다.
     *
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // For keys and refresh tokens (String)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // For values (Object)
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(PostDto.PostResponse.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(PostDto.PostResponse.class));

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }


    /**
     * RedisTemplate을 생성합니다.
     *
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}

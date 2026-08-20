package com.beat.mall.music.app.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppFeignConfiguration {
    @Bean
    public RequestInterceptor appRequestInterceptor(
            @Value("${microservice.internal-token}") String internalToken) {
        return template -> {
            template.header("X-Client-Type", "app");
            template.header("X-Internal-Token", internalToken);
        };
    }
}



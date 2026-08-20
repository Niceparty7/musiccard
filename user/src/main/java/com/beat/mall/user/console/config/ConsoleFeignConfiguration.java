package com.beat.mall.user.console.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsoleFeignConfiguration {
    @Bean
    public RequestInterceptor consoleRequestInterceptor(
            @Value("${microservice.internal-token}") String internalToken) {
        return template -> {
            template.header("X-Internal-Token", internalToken);
        };
    }
}



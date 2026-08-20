package com.beat.mall.music.module.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsoleUserValidationFeignConfiguration {
    @Bean
    public RequestInterceptor consoleUserValidationRequestInterceptor(
            @Value("${microservice.internal-token}") String internalToken) {
        return template -> {
            template.header("X-Client-Type", "console");
            template.header("X-Internal-Token", internalToken);
        };
    }
}

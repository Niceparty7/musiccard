package com.beat.mall.music.console;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final ApplicationArguments applicationArguments;

    public WebMvcConfiguration(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userAuthorityResolver());
    }

    @Bean
    public UserAuthorityResolver userAuthorityResolver() {
        return new UserAuthorityResolver(applicationArguments);
    }
}

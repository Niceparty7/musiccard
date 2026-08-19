package com.beat.mall.music.config;

import com.beat.mall.music.app.UserAuthorityResolver;
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
        resolvers.add(appUserAuthorityResolver());
        resolvers.add(consoleUserAuthorityResolver());
    }

    @Bean
    public UserAuthorityResolver appUserAuthorityResolver() {
        return new UserAuthorityResolver(applicationArguments);
    }

    @Bean
    public com.beat.mall.music.console.UserAuthorityResolver consoleUserAuthorityResolver() {
        return new com.beat.mall.music.console.UserAuthorityResolver(applicationArguments);
    }
}

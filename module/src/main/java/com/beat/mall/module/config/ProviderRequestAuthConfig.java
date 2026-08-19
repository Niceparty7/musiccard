package com.beat.mall.module.config;

import com.beat.mall.common.api.ClientHeaders;
import com.beat.mall.module.auth.ProviderAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ProviderRequestAuthConfig implements WebMvcConfigurer {
    private final ProviderAuthService providerAuthService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Object handler) {
                providerAuthService.checkInternalToken(
                        request.getHeader(ClientHeaders.INTERNAL_TOKEN));
                return true;
            }
        }).addPathPatterns("/**")
                .excludePathPatterns("/druid/**", "/error");
    }
}

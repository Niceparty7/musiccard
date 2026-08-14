package com.beat.mall.module.config;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Druid 监控手动注册
 * 手动创建 @Primary DataSource 后，druid-spring-boot-starter 的
 * DruidDataSourceAutoConfigure 整体退避，StatViewServlet / WebStatFilter
 * 不再自动注册，需在此手动配置以保留 Druid 监控台。
 * 访问：http://localhost:{port}/druid
 */
@Configuration
public class DruidMonitorConfig {
    @Value("${spring.datasource.druid.stat-view-servlet.login-username:admin}")
    private String loginUsername;
    @Value("${spring.datasource.druid.stat-view-servlet.login-password:123456}")
    private String loginPassword;

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean =
                new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        bean.addInitParameter("loginUsername", loginUsername);
        bean.addInitParameter("loginPassword", loginPassword);
        bean.addInitParameter("resetEnable", "false");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> druidWebStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean =
                new FilterRegistrationBean<>(new WebStatFilter());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("exclusions",
                "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return bean;
    }
}
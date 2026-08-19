package com.beat.mall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.beat.mall.user", "com.beat.mall.common"})
@EnableFeignClients(basePackages = {"com.beat.mall.user.app.feign", "com.beat.mall.user.console.feign"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
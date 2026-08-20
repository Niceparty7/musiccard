package com.beat.mall.music.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.beat.mall.music.app", "com.beat.mall.music.config", "com.beat.mall.common"})
@EnableFeignClients(basePackages = "com.beat.mall.music.app.feign")
public class MusicAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicAppApplication.class, args);
    }
}

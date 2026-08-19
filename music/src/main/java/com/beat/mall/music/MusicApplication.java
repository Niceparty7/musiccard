package com.beat.mall.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.beat.mall.music", "com.beat.mall.common"})
@EnableFeignClients(basePackages = {"com.beat.mall.music.app.feign", "com.beat.mall.music.console.feign"})
@EnableScheduling
public class MusicApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicApplication.class, args);
    }
}

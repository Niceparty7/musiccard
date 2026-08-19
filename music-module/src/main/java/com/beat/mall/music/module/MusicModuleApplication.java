package com.beat.mall.music.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.beat.mall.music.module", "com.beat.mall.common"})
@EnableFeignClients(basePackages = "com.beat.mall.music.module.feign")
@EnableScheduling
public class MusicModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicModuleApplication.class, args);
    }
}

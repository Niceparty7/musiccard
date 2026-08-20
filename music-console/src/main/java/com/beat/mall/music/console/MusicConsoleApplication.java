package com.beat.mall.music.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.beat.mall.music.console", "com.beat.mall.music.config", "com.beat.mall.common"})
@EnableFeignClients(basePackages = "com.beat.mall.music.console.feign")
public class MusicConsoleApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicConsoleApplication.class, args);
    }
}

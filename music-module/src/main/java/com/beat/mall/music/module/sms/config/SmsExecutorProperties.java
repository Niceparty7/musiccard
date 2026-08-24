package com.beat.mall.music.module.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms.executor")
public class SmsExecutorProperties {
    private int corePoolSize = 8;
    private int maxPoolSize = 16;
    private int queueCapacity = 100;
}

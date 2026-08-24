package com.beat.mall.music.module.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms.task")
public class SmsTaskProperties {
    private int scanLimit = 20;
    private int maxRetryCount = 2;
    private int retryDelaySeconds = 60;
    private int sendingTimeoutSeconds = 300;
    private int corePoolSize = 8;
    private int maxPoolSize = 16;
    private int queueCapacity = 100;
}

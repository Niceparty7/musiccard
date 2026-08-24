package com.beat.mall.music.module.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms.task")
public class SmsTaskProperties {
    /** 默认关闭，避免应用启动时误发送历史待发送任务。 */
    private boolean enabled = false;
    private String nodeId = "music-module-unknown";
    private int clusterBatchSize = 20;
    private int maxRetryCount = 2;
    private int retryDelaySeconds = 60;
    private int leaseSeconds = 300;
    private int corePoolSize = 8;
    private int maxPoolSize = 16;
    private int queueCapacity = 100;
}

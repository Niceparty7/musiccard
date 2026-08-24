package com.beat.mall.music.module.sms.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms.kafka")
public class SmsKafkaProperties {
    private boolean enabled = false;
    private String topic = "musiccard.sms.send.v1";
    private String dltTopic = "musiccard.sms.send.dlt.v1";
    private String consumerGroup = "musiccard-sms-send-v1";
    private int concurrency = 3;
    private int publishTimeoutSeconds = 10;
    private int backupRetentionDays = 7;
    private int pendingTimeoutSeconds = 300;
    private int processingLeaseSeconds = 300;
    private int maxRetryCount = 2;
    private int retryDelaySeconds = 60;
    private int compensationBatchSize = 100;
}

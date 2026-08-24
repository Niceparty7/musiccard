package com.beat.mall.music.module.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms.compensation")
public class SmsCompensationProperties {
    private boolean enabled = false;
    private int intervalSeconds = 30;
    private String jobName = "smsKafkaCompensationJob";
    private String triggerName = "smsKafkaCompensationTrigger";
    private String group = "SMS";
}

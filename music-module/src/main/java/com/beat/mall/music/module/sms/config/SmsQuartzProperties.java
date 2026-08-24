package com.beat.mall.music.module.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms.quartz")
public class SmsQuartzProperties {
    private boolean enabled = false;
    private int intervalSeconds = 5;
    private String jobName = "smsDispatchJob";
    private String triggerName = "smsDispatchTrigger";
    private String group = "SMS";
}

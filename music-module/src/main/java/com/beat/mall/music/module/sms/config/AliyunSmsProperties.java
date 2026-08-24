package com.beat.mall.music.module.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName = "恒创联众";
    private Integer minuteLimitPerPhone = 3;
    private Integer forbidSeconds = 3600;
}


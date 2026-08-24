package com.beat.mall.music.module.sms.kafka.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsTaskMessage {
    private String version;
    private Long taskId;
    private String phone;
    private String verifyCode;
    private Long createdAt;
    private String traceId;
}

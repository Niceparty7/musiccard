package com.beat.mall.music.module.sms.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsTaskMessage {
    private Long taskId;
    private String messageId;
    private String phone;
    private String content;
    private Short retryCount;
}

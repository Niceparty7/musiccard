package com.beat.mall.common.entity.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsLog {
    private Long id;
    private Long taskId;
    private String phone;
    private String content;
    private String result;
    private String bizId;
    private String requestId;
    private String errorCode;
    private String errorMessage;
    private Integer sendType;
    private Integer status;
    private Short attemptCount;
    private Integer sendTime;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}

package com.beat.mall.common.entity.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsUse {
    private Long id;
    private String phone;
    private Integer minuteStart;
    private Integer requestCount;
    private Integer createTime;
    private Integer updateTime;
}

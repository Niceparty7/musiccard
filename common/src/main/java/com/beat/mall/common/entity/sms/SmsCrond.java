package com.beat.mall.common.entity.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsCrond {
    private Long id;
    private String phone;
    private String content;
    private Short status;
    private Short retryCount;
    private String errorMessage;
    private Integer sendTime;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}

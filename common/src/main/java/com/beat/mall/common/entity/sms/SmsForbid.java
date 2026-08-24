package com.beat.mall.common.entity.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsForbid {
    private Long id;
    private String phone;
    private Integer beginTime;
    private Integer endTime;
    private String reason;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}

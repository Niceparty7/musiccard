package com.beat.mall.common.entity.user;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class UserSign {
    private Long userId;
    private int expiration;
    private String salt;
}

package com.beat.mall.app.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoVo {
    private Long userId;
    private String name;
    private Integer gender;
    private String phone;
    private String avatar;
}

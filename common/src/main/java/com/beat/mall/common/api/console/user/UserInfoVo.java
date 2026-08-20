package com.beat.mall.common.api.console.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoVo {
    private Long userId;
    private String userName;
    private Integer userGender;
    private String userPhone;
    private String userAvatar;
}

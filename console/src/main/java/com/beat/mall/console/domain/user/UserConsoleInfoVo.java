package com.beat.mall.console.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserConsoleInfoVo {
    private Long userId;
    private String userName;
    private String userGender;
    private String userPhone;
    private String userAvatar;
    private Integer userLevel;
}

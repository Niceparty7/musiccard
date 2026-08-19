package com.beat.mall.common.entity.user;

import com.beat.mall.common.utils.BaseUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private String countryCode;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String avatar;
    private String personalProfile;
    private String coverImage;
    private Integer gender;
    private String birthday;
    private String wechatOpenId;
    private String wechatUnionId;
    private String wechatNo;
    private String country;
    private String province;
    private String city;
    private Integer registerTime;
    private String registerIp;
    private Integer lastLoginTime;
    private String lastLoginIp;
    private Integer isBan;
    private String extra;
    private Integer createTime;
    private Integer updateTime = BaseUtil.currentSeconds();
    private Integer isDeleted;
}

package top.yuhanpeng.musiccard.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private String phone;
    private String password;
    private String salt;
    private String name;
    private String avatar;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
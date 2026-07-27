package top.yuhanpeng.musiccard.module.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserVO {
    private Long userId;
}
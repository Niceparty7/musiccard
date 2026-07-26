package top.yuhanpeng.musiccard.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginVO {
    private String sign;
}
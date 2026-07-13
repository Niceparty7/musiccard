package top.yuhanpeng.musiccard.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryInfoVO {
    private String typeName;
    private String typeImage;
    private String typeDesc;
}
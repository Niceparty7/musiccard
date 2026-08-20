package com.beat.mall.common.api.console.tag;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagListVO {
    private String tag;
}

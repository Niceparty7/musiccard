package com.beat.mall.common.api.console.tag;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagListVO {
    private Long id;
    private String tag;
    private String tagDesc;
}

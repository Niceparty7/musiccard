package com.beat.mall.console.domain.tag;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagInfoVO {
    private String tagName;
    private String tagDesc;
}
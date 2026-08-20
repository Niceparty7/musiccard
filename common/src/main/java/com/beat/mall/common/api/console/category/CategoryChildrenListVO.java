package com.beat.mall.common.api.console.category;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryChildrenListVO {
    private String typeName;
    private String typeImage;
}

package com.beat.mall.console.domain.category;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryListVO {
    private String typeName;
    private String typeImage;
}
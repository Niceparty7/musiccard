package com.beat.mall.common.api.console.category;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryListVO {
    private String typeName;
    private String typeImage;
    private List<CategoryChildrenListVO> children;
}

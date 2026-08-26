package com.beat.mall.common.api.console.category;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryListVO {
    private Long id;
    private String typeName;
    private String typeImage;
    private String typeDesc;
    private List<CategoryChildrenListVO> children;
}

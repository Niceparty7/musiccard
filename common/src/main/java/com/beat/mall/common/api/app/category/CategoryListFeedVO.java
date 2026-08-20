package com.beat.mall.common.api.app.category;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryListFeedVO {
    private List<CategoryListVO> list;
}

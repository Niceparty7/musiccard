package com.beat.mall.common.api.console.tag;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TagListFeedVO {
    private List<TagListVO> list;
}

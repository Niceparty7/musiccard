package top.yuhanpeng.musiccard.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryListFeedVO {
    private List<CategoryListVO> list;
}
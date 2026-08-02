package com.beat.mall.console.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProductDto {
    private Long categoryId;
    private String images;
    private String name;
    private Float price;
    private String summary;
}

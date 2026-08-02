package com.beat.mall.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageInfo {
    private String url;
    private Float ar;
}

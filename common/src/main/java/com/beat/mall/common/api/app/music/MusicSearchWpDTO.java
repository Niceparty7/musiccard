package com.beat.mall.common.api.app.music;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicSearchWpDTO {
    private String keyword;
    private Long offset;
}
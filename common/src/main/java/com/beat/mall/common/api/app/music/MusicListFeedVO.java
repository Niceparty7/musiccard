package com.beat.mall.common.api.app.music;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MusicListFeedVO {
    private List<MusicListVO> list;
    private Boolean isEnd;
}

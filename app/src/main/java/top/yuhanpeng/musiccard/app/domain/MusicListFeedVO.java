package top.yuhanpeng.musiccard.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 音乐卡片瀑布流
 */
@Data
@Accessors(chain = true)
public class MusicListFeedVO {
    private List<MusicListVO> list;
    Boolean isEnd;
}

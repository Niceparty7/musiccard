package top.yuhanpeng.musiccard.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 音乐卡片
 */
@Data
@Accessors(chain = true)
public class MusicListVO {
    private Long id;
    private String wallImage;
    private String musicName;
    private String singerName;
    private String musicDesc;
}
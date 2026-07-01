package top.yuhanpeng.musiccard.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 音乐详情
 */
@Data
@Accessors(chain = true)
public class MusicInfoVO {
    private List<String> coverImages;
    private String musicName;
    private String singerName;
    private String albumTitle;
    private String releaseDate;
    private String musicDesc;
}

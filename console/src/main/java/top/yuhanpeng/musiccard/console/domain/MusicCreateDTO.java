package top.yuhanpeng.musiccard.console.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class MusicCreateDTO {
    @NotBlank(message = "音乐封面不能为空")
    private String coverImages;
    @NotBlank(message = "音乐名不能为空")
    private String musicName;
    @NotBlank(message = "歌手名不能为空")
    private String singerName;
    private String albumTitle;
    private String releaseDate;
    private String musicDesc;
}
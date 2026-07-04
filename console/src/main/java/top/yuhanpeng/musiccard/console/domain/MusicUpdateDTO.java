package top.yuhanpeng.musiccard.console.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class MusicUpdateDTO {
    @NotNull(message = "音乐id不能为空")
    private Long id;
    private String coverImages;
    private String musicName;
    private String singerName;
    private String albumTitle;
    private String releaseDate;
    private String musicDesc;
}
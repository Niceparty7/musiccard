package top.yuhanpeng.musiccard.console.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class MusicInfoDTO {
    @NotNull(message = "歌曲id不能为空")
    private Long id;
}
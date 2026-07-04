package top.yuhanpeng.musiccard.console.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class MusicDeleteDTO {
    @NotNull(message = "音乐id不能为空")
    private Long id;
}
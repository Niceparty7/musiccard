package top.yuhanpeng.musiccard.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class MusicListDTO {
    private Integer page = 1;
    private String keyword;
}
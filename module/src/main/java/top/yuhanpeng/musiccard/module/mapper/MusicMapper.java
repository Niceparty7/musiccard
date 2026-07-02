package top.yuhanpeng.musiccard.module.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yuhanpeng.musiccard.module.entity.Music;

import java.util.List;

@Mapper
public interface MusicMapper {
    Music getById(@Param("id") Long id);

    List<Music> getAllMusic();

    int update(@Param("music") Music music);

    Long insert(@Param("music") Music music);

    int delete(@Param("time") Integer time, @Param("id") Long id);
}

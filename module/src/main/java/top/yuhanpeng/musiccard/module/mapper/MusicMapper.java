package top.yuhanpeng.musiccard.module.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yuhanpeng.musiccard.module.entity.Music;

import java.util.List;

@Mapper
public interface MusicMapper {
    Music getById(@Param("id") Long id);

    List<Music> getAllMusic(@Param("offSet") Integer offSet,@Param("pageSize") Integer pageSize,@Param("keyword") String keyword);

    Integer update(@Param("music") Music music);

    Long insert(@Param("music") Music music);

    Integer delete(@Param("time") Integer time, @Param("id") Long id);

    Long countTotal();
}

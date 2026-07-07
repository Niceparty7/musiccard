package top.yuhanpeng.musiccard.module.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.yuhanpeng.musiccard.module.entity.Music;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {
}
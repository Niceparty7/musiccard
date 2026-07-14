package top.yuhanpeng.musiccard.module.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.yuhanpeng.musiccard.module.domain.MusicListDTO;
import top.yuhanpeng.musiccard.module.entity.Music;

import java.util.List;

@Mapper
public interface MusicMapper {
    @Select("select * from music where id=#{id} and is_deleted=0")
    Music getById(@Param("id") Long id);

    @Select("select * from music where id=#{id}")
    Music extractById(@Param("id") Long id);

    List<Music> getAllMusic(@Param("offSet") Integer offSet, @Param("pageSize") Integer pageSize, @Param("keyword") String keyword, @Param("subquery") String subquery);

    List<MusicListDTO> getAllMusicListDTO(@Param("offSet") Integer offSet, @Param("pageSize") Integer pageSize, @Param("keyword") String keyword);

    Integer update(@Param("music") Music music);

    Long insert(@Param("music") Music music);

    Integer delete(@Param("time") Integer time, @Param("id") Long id);

    Long countTotal(@Param("keyword") String keyword);

    @Select("select count(*) from music where type_id=#{typeId} and is_deleted=0")
    Long getByTypeId(@Param("typeId") Long typeId);

    @Select("select id from category where is_deleted=0 and type_name like concat('%',#{keyword},'%')")
    List<Long> getIds(@Param("keyword") String keyword);
}
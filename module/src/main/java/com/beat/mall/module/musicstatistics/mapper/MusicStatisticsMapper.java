package com.beat.mall.module.musicstatistics.mapper;

import com.beat.mall.module.musicstatistics.entity.MusicStatistics;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MusicStatisticsMapper {

    /**
     * 查询某天（含当日）最近一条统计记录的音乐数量（累计总量）
     * 命中联合索引三段，取 <= 目标日的最新累计值
     */
    @Select("SELECT music_count FROM music_statistics " +
            "WHERE year=#{year} AND month=#{month} AND day<=#{day} " +
            "ORDER BY day DESC LIMIT 1")
    Integer getByDay(@Param("year") String year, @Param("month") String month, @Param("day") String day);

    /**
     * 查询某年某月最近一条统计记录的音乐数量（累计总量）
     * 命中联合索引前两段，取 <= 目标月的最新累计值
     */
    @Select("SELECT music_count FROM music_statistics " +
            "WHERE year=#{year} AND month=#{month} " +
            "ORDER BY day DESC LIMIT 1")
    Integer getByMonth(@Param("year") String year, @Param("month") String month);

    /**
     * 查询某年最近一条统计记录的音乐数量（累计总量）
     * 命中联合索引第一段，取 <= 目标年的最新累计值
     */
    @Select("SELECT music_count FROM music_statistics " +
            "WHERE year=#{year} " +
            "ORDER BY month DESC, day DESC LIMIT 1")
    Integer getByYear(@Param("year") String year);

    /**
     * 写入统计记录：存在则更新，不存在则插入（依赖 uk_date 唯一索引）
     */
    @Insert("INSERT INTO music_statistics(year,month,day,music_count,create_time) " +
            "VALUES(#{year},#{month},#{day},#{musicCount},#{createTime}) " +
            "ON DUPLICATE KEY UPDATE music_count=VALUES(music_count), create_time=VALUES(create_time)")
    Integer upsert(MusicStatistics stat);
}
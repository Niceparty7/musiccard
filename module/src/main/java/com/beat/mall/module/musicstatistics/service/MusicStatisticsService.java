package com.beat.mall.module.musicstatistics.service;

import com.beat.mall.module.music.service.MusicService;
import com.beat.mall.module.musicstatistics.entity.MusicStatistics;
import com.beat.mall.module.musicstatistics.mapper.MusicStatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MusicStatisticsService {

    private final MusicStatisticsMapper musicStatisticsMapper;
    private final MusicService musicService;

    /**
     * 查询当年/当月/当日音乐数量（累计总量）
     * 每条统计记录存的是"截至该日的全部音乐数量"，查询取该维度最近一条记录的值
     */
    public Map<String, Object> getCurrentStatistics() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());

        Integer yearCount = musicStatisticsMapper.getByYear(year);
        Integer monthCount = musicStatisticsMapper.getByMonth(year, month);
        Integer dayCount = musicStatisticsMapper.getByDay(year, month, day);

        // 使用 LinkedHashMap 保证序列化字段按插入顺序输出：year → yearCount → month → monthCount → day → dayCount
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", year);
        result.put("yearCount", yearCount != null ? yearCount : 0);
        result.put("month", month);
        result.put("monthCount", monthCount != null ? monthCount : 0);
        result.put("day", day);
        result.put("dayCount", dayCount != null ? dayCount : 0);
        return result;
    }

    /**
     * 每天凌晨 2 点执行：统计截至昨日（yesterday 23:59:59）的全部音乐数量并写入统计表
     * music_count 存累计总量（非当日新增）
     */
    public void dailyStatistic() {
        // 统计的是截至昨日 23:59:59 的全部音乐数量
        LocalDate yesterday = LocalDate.now().minusDays(1);

        Long count = musicService.countAll();

        int nowEpochSecond = (int) (LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        MusicStatistics stat = new MusicStatistics()
                .setYear(String.valueOf(yesterday.getYear()))
                .setMonth(String.format("%02d", yesterday.getMonthValue()))
                .setDay(String.format("%02d", yesterday.getDayOfMonth()))
                .setMusicCount(count == null ? 0 : count.intValue())
                .setCreateTime(nowEpochSecond)
                .setUpdateTime(nowEpochSecond)
                .setIsDeleted(0);
        musicStatisticsMapper.upsert(stat);
    }
}
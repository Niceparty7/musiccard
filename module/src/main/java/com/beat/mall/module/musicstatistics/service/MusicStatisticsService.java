package com.beat.mall.module.musicstatistics.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.beat.mall.module.music.service.MusicService;
import com.beat.mall.module.musicstatistics.entity.MusicStatistics;
import com.beat.mall.module.musicstatistics.mapper.MusicStatisticsMapper;
import com.beat.mall.module.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MusicStatisticsService {
    private static final String STATISTICS_CACHE_PREFIX = "console:music:statistics:";
    private final MusicStatisticsMapper musicStatisticsMapper;
    private final MusicService musicService;
    private final RedisUtil redisUtil;

    /**
     * 查询当年/当月/当日音乐数量（累计总量），优先读 Redis 缓存，未命中回源 MySQL 并写回
     */
    public Map<String, Object> getCurrentStatistics() {
        LocalDate now = LocalDate.now();
        String cacheKey = STATISTICS_CACHE_PREFIX + now;
        // ===== 1. 读缓存 =====
        String cachedJson = redisUtil.get(cacheKey);
        if (cachedJson != null) {
            return JSON.parseObject(cachedJson, new TypeReference<LinkedHashMap<String, Object>>() {
            });
        }
        // ===== 2. 缓存未命中，回源 MySQL（3 条 SQL）=====
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
        // ===== 3. 写回缓存，TTL 到当日 24:00 =====
        LocalDateTime midnight = now.plusDays(1).atStartOfDay();
        int ttl = (int) Duration.between(LocalDateTime.now(), midnight).getSeconds();
        redisUtil.setex(cacheKey, ttl, JSON.toJSONString(result));
        return result;
    }

    /**
     * 每天凌晨 2 点执行：统计截至昨日（yesterday 23:59:59）的全部音乐数量并写入统计表
     * 写库成功后删除统计缓存，保证次日首次查询回源刷新
     */
    public void dailyStatistic() {
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
        // 写库成功后删除统计缓存，次日首次查询回源刷新
        redisUtil.del(STATISTICS_CACHE_PREFIX + LocalDate.now());
    }
}
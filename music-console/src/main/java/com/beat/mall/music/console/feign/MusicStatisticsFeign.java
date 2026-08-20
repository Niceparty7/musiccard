package com.beat.mall.music.console.feign;

import com.beat.mall.music.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "music-module", contextId = "consoleMusicStatisticsFeign", configuration = ConsoleFeignConfiguration.class)
public interface MusicStatisticsFeign {
    @GetMapping("/music/statistics")
    Response<Map<String, Object>> getStatistics(@RequestHeader("X-User-Id") Long userId);
}



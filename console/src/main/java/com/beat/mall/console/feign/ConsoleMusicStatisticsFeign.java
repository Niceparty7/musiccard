package com.beat.mall.console.feign;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "module", contextId = "consoleMusicStatisticsFeign")
public interface ConsoleMusicStatisticsFeign {
    @GetMapping("/music/statistics")
    Response<Map<String, Object>> getStatistics(@RequestHeader("X-User-Id") Long userId);
}

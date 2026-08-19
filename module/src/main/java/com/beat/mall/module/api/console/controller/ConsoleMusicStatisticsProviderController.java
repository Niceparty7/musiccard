package com.beat.mall.module.api.console.controller;

import com.beat.mall.common.response.Response;
import com.beat.mall.module.auth.ProviderAuthService;
import com.beat.mall.module.musicstatistics.service.MusicStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ConsoleMusicStatisticsProviderController {
    private final MusicStatisticsService musicStatisticsService;
    private final ProviderAuthService providerAuthService;

    @RequestMapping(value = "/music/statistics", headers = "X-Client-Type=console")
    public Response<Map<String, Object>> getStatistics(
            @RequestHeader("X-User-Id") Long userId) {
        providerAuthService.requireUser(userId);
        return new Response<>(1001, musicStatisticsService.getCurrentStatistics());
    }
}

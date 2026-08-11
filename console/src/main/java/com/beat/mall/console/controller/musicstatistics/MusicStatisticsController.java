package com.beat.mall.console.controller.musicstatistics;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.module.musicstatistics.service.MusicStatisticsService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicStatisticsController {

    private final MusicStatisticsService musicStatisticsService;

    /**
     * 查询当年/当月/当日音乐数量
     */
    @RequestMapping("/statistics")
    public Response getStatistics(@VerifiedUser User loginUser) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        boolean success = true;
        Map<String, Object> data = null;
        try {
            data = musicStatisticsService.getCurrentStatistics();
        } catch (Exception e) {
            success = false;
            log.error("get music statistics fail", e);
        }
        if (!success) {
            return new Response(4005);
        }
        return new Response(1001, data);
    }
}
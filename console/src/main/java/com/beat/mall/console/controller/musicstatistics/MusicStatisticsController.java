package com.beat.mall.console.controller.musicstatistics;

import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.ConsoleMusicStatisticsFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MusicStatisticsController {
    private final ConsoleMusicStatisticsFeign statisticsFeign;

    @GetMapping("/music/statistics")
    public Response<Map<String, Object>> getStatistics(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : statisticsFeign.getStatistics(loginUser.getId());
    }
}

package com.beat.mall.music.console.controller.musicstatistics;

import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.music.console.annotations.VerifiedUser;
import com.beat.mall.music.console.feign.MusicStatisticsFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RestController("consoleMusicStatisticsController")
@RequiredArgsConstructor
public class MusicStatisticsController {
    private final MusicStatisticsFeign statisticsFeign;

    @GetMapping("/music/statistics")
    public Response<Map<String, Object>> getStatistics(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : statisticsFeign.getStatistics(loginUser.getId());
    }
}



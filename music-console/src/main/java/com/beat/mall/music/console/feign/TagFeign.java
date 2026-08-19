package com.beat.mall.music.console.feign;

import com.beat.mall.music.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.api.console.tag.TagInfoVO;
import com.beat.mall.common.api.console.tag.TagListFeedVO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "music-module", contextId = "consoleTagFeign", configuration = ConsoleFeignConfiguration.class)
public interface TagFeign {
    @GetMapping("/tag/info")
    Response<TagInfoVO> getInfo(@RequestHeader("X-User-Id") Long userId,
                                @RequestParam("id") Long id);

    @GetMapping("/tag/list")
    Response<TagListFeedVO> getList(@RequestHeader("X-User-Id") Long userId);

    @PostMapping("/tag/create")
    Response<String> create(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("tagName") String tagName,
                            @RequestParam(value = "tagDesc", required = false) String tagDesc);

    @PutMapping("/tag/update")
    Response<String> update(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("id") Long id,
                            @RequestParam("tagName") String tagName,
                            @RequestParam(value = "tagDesc", required = false) String tagDesc);

    @DeleteMapping("/tag/delete")
    Response<String> delete(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("id") Long id);
}



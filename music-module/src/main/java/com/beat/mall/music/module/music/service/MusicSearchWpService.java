package com.beat.mall.music.module.music.service;

import com.alibaba.fastjson.JSON;
import com.beat.mall.common.api.app.music.MusicSearchWpDTO;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MusicSearchWpService {
    public String encode(MusicSearchWpDTO wpDTO) throws Exception {
        validate(wpDTO);
        String json = JSON.toJSONString(wpDTO);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public MusicSearchWpDTO decode(String wp) throws Exception {
        if (wp == null || wp.trim().isEmpty()) {
            throw new Exception("wp不能为空");
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(wp);
            String json = new String(bytes, StandardCharsets.UTF_8);
            MusicSearchWpDTO wpDTO = JSON.parseObject(json, MusicSearchWpDTO.class);
            validate(wpDTO);
            return wpDTO;
        } catch (Exception exception) {
            throw new Exception("wp参数不合法", exception);
        }
    }

    private void validate(MusicSearchWpDTO wpDTO) throws Exception {
        if (wpDTO == null) {
            throw new Exception("wp对象不能为空");
        }
        if (wpDTO.getPage() == null || wpDTO.getPage() < 1) {
            throw new Exception("wp页码不合法");
        }
    }
}
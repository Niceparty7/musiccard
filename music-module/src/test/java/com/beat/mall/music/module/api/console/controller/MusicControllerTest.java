package com.beat.mall.music.module.api.console.controller;

import com.beat.mall.common.api.console.music.MusicInfoVO;
import com.beat.mall.common.entity.music.Music;
import com.beat.mall.common.response.Response;
import com.beat.mall.music.module.auth.AuthService;
import com.beat.mall.music.module.category.service.CategoryService;
import com.beat.mall.music.module.music.service.BaseMusicService;
import com.beat.mall.music.module.music.service.MusicService;
import com.beat.mall.music.module.redis.util.RedisUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MusicControllerTest {
    @Test
    void getMusicInfoSupportsMusicWithoutCategory() throws Exception {
        MusicService musicService = mock(MusicService.class);
        CategoryService categoryService = mock(CategoryService.class);
        BaseMusicService baseMusicService = mock(BaseMusicService.class);
        AuthService authService = mock(AuthService.class);
        MusicController controller = new MusicController(
                musicService,
                categoryService,
                baseMusicService,
                mock(RedisUtil.class),
                authService);
        Music music = new Music()
                .setId(56L)
                .setCoverImages("https://example.com/cover.jpg")
                .setMusicName("new music")
                .setSingerName("artist")
                .setCreateTime(1)
                .setUpdateTime(1)
                .setTypeId(null);
        when(musicService.getById(56L)).thenReturn(music);
        when(baseMusicService.getTagsByMusicId(56L)).thenReturn(List.of());

        Response<MusicInfoVO> response = controller.getMusicInfo(1L, 56L);

        assertEquals(1001, response.getStatus().getCode());
        assertNull(response.getResult().getTypeId());
        assertNull(response.getResult().getTypeName());
        assertNull(response.getResult().getTypeImage());
        verifyNoInteractions(categoryService);
    }
}

package top.yuhanpeng.musiccard.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.app.domain.MusicInfoVO;
import top.yuhanpeng.musiccard.app.domain.MusicListFeedVO;
import top.yuhanpeng.musiccard.app.domain.MusicListVO;
import top.yuhanpeng.musiccard.module.domain.MusicCategoryDTO;
import top.yuhanpeng.musiccard.module.service.CategoryService;
import top.yuhanpeng.musiccard.module.service.MusicService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/music/info")
    public MusicInfoVO getMusicInfoById(@RequestParam(value = "id") Long id) {
        MusicCategoryDTO musicCategoryDTO = null;
        Boolean res = true;
        try {
            musicCategoryDTO = musicService.getMusicWithCategoryById(id);
        } catch (Exception e) {
            log.error("cannot find the id!");
            res = false;
        }
        if (!res) {
            return null;
        }
        List<String> coverImagesString = Arrays.stream(musicCategoryDTO.getCoverImages().split("\\$")).toList();
        MusicInfoVO musicInfoVO = new MusicInfoVO();
        musicInfoVO.setCoverImages(coverImagesString)
                .setMusicName(musicCategoryDTO.getMusicName())
                .setSingerName(musicCategoryDTO.getSingerName())
                .setAlbumTitle(musicCategoryDTO.getAlbumTitle())
                .setReleaseDate(musicCategoryDTO.getReleaseDate())
                .setMusicDesc(musicCategoryDTO.getMusicDesc())
                .setTypeName(musicCategoryDTO.getTypeName())
                .setTypeImage(musicCategoryDTO.getTypeImage());
        log.info(musicInfoVO.toString());
        return musicInfoVO;
    }

    @RequestMapping("/music/list")
    public MusicListFeedVO getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        keyword = keyword == null ? keyword : keyword.trim();
        List<MusicCategoryDTO> list = musicService.getAllMusicWithCategory(page, pageSize, keyword);
        Boolean isEnd = list.size() < pageSize;
        for (MusicCategoryDTO musicCategoryDTO : list) {
            String[] coverImages = musicCategoryDTO.getCoverImages().split("\\$");
            MusicListVO musicListVO = new MusicListVO();
            musicListVO.setId(musicCategoryDTO.getId())
                    .setWallImage(coverImages[0])
                    .setMusicName(musicCategoryDTO.getMusicName())
                    .setSingerName(musicCategoryDTO.getSingerName())
                    .setMusicDesc(musicCategoryDTO.getMusicDesc())
                    .setTypeName(musicCategoryDTO.getTypeName());
            musicCardList.add(musicListVO);
        }
        MusicListFeedVO musicListFeedVO = new MusicListFeedVO();
        musicListFeedVO.setList(musicCardList)
                .setIsEnd(isEnd);
        log.info(musicListFeedVO.toString());
        return musicListFeedVO;
    }
}
package top.yuhanpeng.musiccard.module.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yuhanpeng.musiccard.module.domain.MusicExcelDTO;
import top.yuhanpeng.musiccard.module.domain.MusicListDTO;
import top.yuhanpeng.musiccard.module.entity.Category;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.listener.MusicExcelListener;
import top.yuhanpeng.musiccard.module.mapper.MusicMapper;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final Executor excelExecutor;
    @Resource
    private MusicMapper musicMapper;
    @Resource
    private CategoryService categoryService;

    public Music getById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = musicMapper.getById(id);
        if (music == null) {
            throw new RuntimeException("music is null!");
        }
        return music;
    }

    public Music extractById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = musicMapper.extractById(id);
        if (music == null) {
            throw new RuntimeException("music is null!");
        }
        return music;
    }

    public List<Music> getAllMusic(Integer page, Integer pageSize, String keyword) {
        List<Long> ids = musicMapper.getIds(keyword);
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < ids.size(); i++) {
            if (i == ids.size() - 1) {
                stringBuffer.append(ids.get(i) + "");
                break;
            }
            stringBuffer.append(ids.get(i) + "").append(",");
        }
        String subquery = stringBuffer.toString();
        return musicMapper.getAllMusic((page - 1) * pageSize, pageSize, keyword, subquery);
    }

    public List<MusicListDTO> getAllMusicListDTO(Integer page, Integer pageSize, String keyword) {
        return musicMapper.getAllMusicListDTO((page - 1) * pageSize, pageSize, keyword);
    }

    public Long countTotal(String keyword) {
        return musicMapper.countTotal(keyword);
    }

    public Long create(String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate, Integer typeId)
            throws Exception {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music()
                .setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0)
                .setTypeId(typeId);
        if (coverImages == null) {
            throw new RuntimeException("coverImages cannot be null!");
        }
        if (musicName == null) {
            throw new RuntimeException("musicName cannot be null!");
        }
        if (singerName == null) {
            throw new RuntimeException("singerName cannot be null!");
        }
        musicMapper.insert(music);
        Long resId = music.getId();
        return resId;
    }

    public Long update(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate, Integer typeId)
            throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music()
                .setId(id)
                .setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0)
                .setTypeId(typeId);
        if (extractById(id) == null) {
            throw new RuntimeException("cannot find the id");
        }
        Long affectedRows = (long) musicMapper.update(music);
        return affectedRows;
    }

    public Long edit(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate, Integer typeId)
            throws Exception {
        Long res;
        if (typeId != null) {
            Category category = categoryService.getById((long) typeId);
            if (category == null) {
                throw new RuntimeException("cannot find the typeId");
            }
        }
        if (id != null) {
            res = update(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate, typeId);
            if (res == 0) {
                throw new RuntimeException("update fail!");
            }
        } else {
            res = create(coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate, typeId);
            if (res == null) {
                throw new RuntimeException("create fail!");
            }
        }
        return res;
    }

    public Integer delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return musicMapper.delete(timeStamp, id);
    }

    public Long getByTypeId(Long typeId) {
        return musicMapper.getByTypeId(typeId);
    }

    public void export(OutputStream outputStream) throws Exception {
        List<Music> list = musicMapper.getAllMusicList();
        List<MusicExcelDTO> excelList = list.stream()
                .map(item -> {
                    MusicExcelDTO musicExcelDTO = new MusicExcelDTO();
                    musicExcelDTO.setMusicName(item.getMusicName());
                    musicExcelDTO.setSingerName(item.getSingerName());
                    musicExcelDTO.setCoverImages(item.getCoverImages());
                    musicExcelDTO.setMusicDesc(item.getMusicDesc());
                    musicExcelDTO.setAlbumTitle(item.getAlbumTitle());
                    musicExcelDTO.setReleaseDate(item.getReleaseDate());
                    musicExcelDTO.setIsDeleted(item.getIsDeleted());
                    musicExcelDTO.setTypeId(item.getTypeId());
                    musicExcelDTO.setCreateTime(item.getCreateTime());
                    musicExcelDTO.setUpdateTime(item.getUpdateTime());
                    return musicExcelDTO;
                }).toList();
        EasyExcel.write(outputStream, MusicExcelDTO.class)
                .sheet("音乐数据")
                .doWrite(excelList);
    }

    public void upload(InputStream inputStream) throws Exception {
        EasyExcel.read(inputStream, MusicExcelDTO.class, new MusicExcelListener(musicMapper)).sheet().doRead();
    }

    public File exportZip() throws Exception {
        List<CompletableFuture<File>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int mod = i;
            CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
                        List<Music> list = musicMapper.selectByMod(mod);
                        File file = new File("music_" + mod + ".xlsx");
                        EasyExcel.write(file, MusicExcelDTO.class)
                                .sheet("音乐数据")
                                .doWrite(convert(list));
                        return file;
                    },
                    excelExecutor
            );
            futures.add(future);
        }
        List<File> files = futures.stream()
                .map(CompletableFuture::join)
                .toList();
        File zipFile = new File("music.zip");
        ZipUtil.zip(zipFile, true, files.toArray(new File[0]));
        return zipFile;
    }

    private List<MusicExcelDTO> convert(List<Music> list) {
        return list.stream()
                .map(item -> {
                    MusicExcelDTO dto = new MusicExcelDTO();
                    dto.setMusicName(item.getMusicName());
                    dto.setSingerName(item.getSingerName());
                    dto.setCoverImages(item.getCoverImages());
                    dto.setMusicDesc(item.getMusicDesc());
                    dto.setAlbumTitle(item.getAlbumTitle());
                    dto.setReleaseDate(item.getReleaseDate());
                    dto.setCreateTime(item.getCreateTime());
                    dto.setUpdateTime(item.getUpdateTime());
                    dto.setTypeId(item.getTypeId());
                    return dto;
                }).toList();
    }

    public void uploadZip(MultipartFile multipartFile) throws Exception {
        File zip = File.createTempFile("music", ".zip");
        multipartFile.transferTo(zip);
        File dir = new File("temp/music");
        ZipUtil.unzip(zip, dir);
        List<File> files = FileUtil.loopFiles(dir, pathname -> pathname.getName().endsWith(".xlsx"));
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (File file : files) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        EasyExcel.read(file, MusicExcelDTO.class, new MusicExcelListener(musicMapper))
                                .sheet()
                                .doRead();
                    },
                    excelExecutor
            );
            futures.add(future);
        }
        futures.forEach(CompletableFuture::join);
    }
}
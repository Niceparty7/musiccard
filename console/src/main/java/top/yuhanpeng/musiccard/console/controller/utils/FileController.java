package top.yuhanpeng.musiccard.console.controller.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.yuhanpeng.musiccard.module.service.FileService;

@RestController
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        String res = null;
        try {
            res = fileService.uploadAndSave(file);
        } catch (Exception e) {
            res = "文件上传失败";
            log.error("文件上传失败", e);
        }
        return res;
    }
}
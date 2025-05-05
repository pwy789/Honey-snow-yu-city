package com.pwy.controller;

import cn.hutool.core.lang.UUID;
import com.pwy.common.Result;
import com.pwy.utils.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@RestController
public class UploadController {
    @Value("${spring.servlet.multipart.max-file-size}")
    private  String FILE_MAX_SIZE;
    @Resource
    private MinioService minioService;
    @PostMapping("/upload")
    public Result uploadToMinio(@RequestParam("file") MultipartFile file,String theme) throws Exception {
            int index = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
            //拿到文件后缀名
            String sub = file.getOriginalFilename().substring(index);
            //拿到文件类型
            String fileType = file.getContentType();

            //获取随机字符串做成前缀
            String pre = UUID.randomUUID().toString(true);
            String newFileName=pre+sub;
           String fileURL = minioService.uploadFile(newFileName, file,fileType,theme);

        return  Result.success(fileURL,"文件上传成功!");
    }
}

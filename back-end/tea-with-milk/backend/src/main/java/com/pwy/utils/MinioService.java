package com.pwy.utils;

import com.pwy.common.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Component
public class MinioService {
    @Autowired
    private MinioClient client;
    @Autowired
    private MinioProperties properties;
    private final String separator = "/";

    //创建桶
    public void createBucket(String bucketName) throws Exception {
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            //桶不存在
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    //生成文件url
    public String createFileUrl(String fileName,String theme) {


        //时间文件夹
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return separator +theme+separator+ time + separator + fileName;

    }

    //上传文件
    public String uploadFile(String fileName, MultipartFile file,String fileType,String theme) throws Exception {
        InputStream inputStream = file.getInputStream();
        String imgURL = createFileUrl(fileName,theme);
        PutObjectArgs putObj = PutObjectArgs.builder().
                contentType(fileType).object(imgURL).
                bucket(properties.
                        getBucket()).stream(inputStream, inputStream.available(), -1).build();
        client.putObject(putObj);
        //返回这个文件的url

        return properties.getReadPath() + separator + properties.getBucket() + imgURL;

    }
}

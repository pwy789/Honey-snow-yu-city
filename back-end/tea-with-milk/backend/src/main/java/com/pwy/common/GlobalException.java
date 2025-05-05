package com.pwy.common;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalException {
    @Value("${spring.servlet.multipart.max-file-size}")
    private  String FILE_MAX_SIZE;
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public Result handleMaxSizeException(){

        return Result.error("文件最大上传大小为"+FILE_MAX_SIZE);
    }
}

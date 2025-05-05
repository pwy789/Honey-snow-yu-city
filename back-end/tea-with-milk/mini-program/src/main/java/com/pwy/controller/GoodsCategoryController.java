package com.pwy.controller;

import com.pwy.common.Result;
import com.pwy.entity.pojo.GoodsCategory;
import com.pwy.service.GoodsCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/goodscategory")
public class GoodsCategoryController {
    @Autowired
    private GoodsCategoryService categoryService;


    //获取所有分类
    @GetMapping("/list")
    public Result getCategoryList(){
        List<GoodsCategory> list = categoryService.getCategoryList();
        return Result.success(list);
    }

}

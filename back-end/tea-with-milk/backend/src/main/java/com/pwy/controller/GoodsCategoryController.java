package com.pwy.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.pwy.common.Result;
import com.pwy.entity.pojo.GoodsCategory;
import com.pwy.service.GoodsCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goodscategory")
public class GoodsCategoryController {
    @Autowired
    private GoodsCategoryService categoryService;
    //添加分类
    @PostMapping("/add")
    public Result getCategoryList(@RequestBody GoodsCategory goodsCategory){
       log.info("goodsCategory: {}",goodsCategory);
       if(categoryService.save(goodsCategory)) {
           return Result.success("添加成功");
       }
        return  Result.error("添加失败,请稍后重试");
    }
    //获取所有分类
    @GetMapping("/list")
    public Result getCategoryList(){
        List<GoodsCategory> list = categoryService.list(Wrappers.<GoodsCategory>lambdaQuery().orderByAsc(GoodsCategory::getSort));
        if(list.isEmpty()){
            return Result.success();
        }
        return Result.success(list);
    }

    //修改分类状态
    @PutMapping("/status/{id}")
    public Result setStatus(@PathVariable("id") String id ,Short status){
        GoodsCategory goodsCategory=new GoodsCategory();
        goodsCategory.setId(id);
        goodsCategory.setStatus(status);
        if (categoryService.updateById(goodsCategory)) {
            return  Result.success("修改成功");
        }
        return  Result.error("修改失败,请稍后尝试!");
    }
    //修改分类信息
    @PutMapping("/update")
    public Result updateCategory(@RequestBody GoodsCategory goodsCategory){
       if(categoryService.updateById(goodsCategory)){
           return  Result.success("修改成功");
       }
        return  Result.error("修改失败,请稍后尝试!");
    }
}

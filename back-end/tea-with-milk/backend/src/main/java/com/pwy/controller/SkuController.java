package com.pwy.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Sku;
import com.pwy.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/sku")
public class SkuController {
    @Autowired
    private SkuService service;
    //根据传递来的维度id查出其所有的Sku
    @GetMapping("/{dimensionId}")
    public Result getSkusByDimensionId(@PathVariable Integer dimensionId){

        List<Sku> skus = service.lambdaQuery().eq(Sku::getDimensionId, dimensionId).list();
        return Result.success(skus);
    }
    //根据id删除某个sku
    @DeleteMapping("/{id}")
    public Result delSkuById(@PathVariable("id")Integer id){
        if(service.removeById(id)){
            return Result.success("删除成功");
        }
        return Result.error("删除失败,请稍后重试");
    }
    //添加sku
    @PostMapping("/add")
    public Result addSku(@RequestBody Sku sku){
       if(service.save(sku)){
           return Result.success("添加成功");
       }
       return  Result.error("添加失败,请稍后重试");
    }
  //修改sku
    @PutMapping("/update")
    public  Result updateSku(@RequestBody Sku sku){
        UpdateWrapper<Sku> uw=new UpdateWrapper<>();

        uw.eq("id",sku.getId());
        if (service.update(sku,uw)) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败,请稍后重试");
    }
}

package com.pwy.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pwy.common.GoodsAndCategoryStatus;
import com.pwy.common.PaginateResultSet;

import com.pwy.common.Result;
import com.pwy.entity.dto.GoodsInfoDto;
import com.pwy.entity.dto.GoodsSearchDto;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.vo.GoodsVo;
import com.pwy.service.GoodsCategoryService;
import com.pwy.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsCategoryService goodsCategoryService;
    //添加商品
    @PostMapping("/add")
    public Result addGoods(@RequestBody Goods goods){
        goodsService.add(goods);
            return Result.success("添加成功");


    }
   //获取商品列表
    @PostMapping("/list")
    public Result getGoodsList(@RequestBody GoodsSearchDto dto){
        Page<Goods> val  = goodsService.getGoodsList(dto);
        PaginateResultSet<List<Goods>> prs= new PaginateResultSet<>(val.getRecords(), val.getTotal());
        return  Result.success(prs);
    }
    //根据id查询商品
    @GetMapping("/{id}")
    public Result getGoodsById(@PathVariable String id){
        GoodsVo gv = goodsService.getGoodsById(id);
        return Result.success(gv);
    }
    //修改商品
    @PutMapping("/update")
    public Result updateGoods(@RequestBody GoodsInfoDto dto){
        goodsService.updateGoods(dto);
        return Result.success("修改成功");
    }
    @DeleteMapping("/delete")
    public Result delGoodsIsChecked(@RequestParam("ids") List<String> Ids){

        return    goodsService.delGoodsByIds(Ids);
    }
    //修改商品状态
    @PutMapping("/status/{id}/{status}")
    public Result setGoodsStatus(@PathVariable String id,@PathVariable Short status){

        if(Objects.equals(status, GoodsAndCategoryStatus.BEGIN_TO_SELL)){
            Goods goods = goodsService.getById(id);
            //起售
            String categoryId = goods.getCategoryId();
            short categoryStatus = goodsCategoryService.getById(categoryId).getStatus();
            if(Objects.equals(categoryStatus,GoodsAndCategoryStatus.BAN_THE_SALE_OF)) return Result.error("请先启用该商品分类");
        }
        goodsService.update(Wrappers.<Goods>lambdaUpdate().set(Goods::getStatus,status).eq(Goods::getId,id));
        return Result.success("修改成功");

    }

}

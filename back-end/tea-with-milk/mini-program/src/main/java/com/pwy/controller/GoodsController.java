package com.pwy.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pwy.common.GoodsAndCategoryStatus;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.vo.GoodsDetailVo;
import com.pwy.entity.vo.GoodsSearch;
import com.pwy.entity.vo.GuessLikeVo;
import com.pwy.entity.vo.GuessSearch;
import com.pwy.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/goods")
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    //获取商品列表
    @GetMapping("/list")
    public Result getGoodsList(){

        return goodsService.getGoodsList();
    }
    //根据id查询商品
    @GetMapping
    public Result getGoodsDetail(String id){

        GoodsDetailVo gdv = goodsService.getGoodsDetailById(id);
        return Result.success(gdv);
    }
    //检查商品是否有sku
    @GetMapping("/check/{id}")
    public Result checkGoodsHasSku(@PathVariable String id){
       return Result.success(goodsService.checkGoodsHasSku(id));
    }
    //返回猜你喜欢数据
    @GetMapping("/guesslike")
    public  Result guessLike(){
        LambdaQueryWrapper<Goods> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Goods::getStatus, GoodsAndCategoryStatus.BEGIN_TO_SELL);
        long count = goodsService.count(lqw);


        if(count<5){
            Page<Goods> p=new Page<>(1,count);
            List<Goods> goods = goodsService.page(p,lqw).getRecords();
            return  Result.success(BeanUtil.copyToList(goods, GuessLikeVo.class));
        }else {
            long totalPages = (count + 4) / 5; // 计算总页数
            Random r = new Random();
            int randomPage = r.nextInt((int) totalPages) + 1; // 生成 1 到 totalPages 之间的随机页码
            Page<Goods> p = new Page<>(randomPage, 5);
            IPage<Goods> pageResult = goodsService.page(p,lqw);
            List<Goods> goods = pageResult.getRecords();
            List<GuessLikeVo> vos = BeanUtil.copyToList(goods, GuessLikeVo.class);
            Collections.shuffle(vos);
            return Result.success(vos);
        }

    }
    //返回猜你想搜数据
    @GetMapping("/guesssearch")
    public Result guessSearch(){
        List<GuessSearch> guessSearches = goodsService.guessSearch();
        //商品数量
        int size = guessSearches.size();
        //打乱集合
        Collections.shuffle(guessSearches);
        List<GuessSearch> collect;
        if(size<10){
            collect = guessSearches;
        }else {
            collect = guessSearches.stream().limit(10).collect(Collectors.toList());
        }

        return Result.success(collect);
    }
    //返回搜索的商品数据
    @GetMapping("/search")
    public Result search(String keyWord){
        List<GoodsSearch> list = goodsService.search(keyWord);
        return Result.success(list);
    }
}

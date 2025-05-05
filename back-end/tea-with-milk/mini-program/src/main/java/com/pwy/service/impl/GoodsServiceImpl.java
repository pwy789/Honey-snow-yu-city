package com.pwy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.GoodsAndCategoryStatus;
import com.pwy.common.RedisKeyConstant;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.pojo.GoodsCategory;
import com.pwy.entity.vo.*;
import com.pwy.mapper.GoodsMapper;
import com.pwy.service.GoodsCategoryService;
import com.pwy.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsCategoryService goodsCategoryService;
    @Override
    public GoodsDetailVo getGoodsDetailById(String id) {
        String key=RedisKeyConstant.GOODS_DETAIL_KEY + id;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))){
             return  JSONUtil.toBean(redisTemplate.opsForValue().get(key),GoodsDetailVo.class);
        }
        GoodsDetailVo gdv = getGoodsDetailVo(id);
        //存入redis并设置过期时间
         redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(gdv), RedisKeyConstant.GOODS_DETAIL_EXPIRE, TimeUnit.MINUTES);

     return gdv;
    }
    //查询数据库
    private GoodsDetailVo getGoodsDetailVo(String id) {
        //首先查询出商品的基本信息
        Goods goods = getById(id);
        //查询出商品包含的图片
        List<String> pictures = goodsMapper.getGoodsPictures(id);
        //将封面图放在最前
        String temp;
        for (int i = 0; i < pictures.size(); i++) {
             if(pictures.get(i).equals(goods.getCoverImage())){
                temp= pictures.get(0);
               pictures.set(i,temp);
               pictures.set(0,pictures.get(i));
               break;
             }
        }
        GoodsDetailVo gdv = BeanUtil.copyProperties(goods, GoodsDetailVo.class);
        gdv.setImages(pictures);
        //查询出商品的维度及其sku
        List<DimensionSkuVo> dsList = goodsMapper.getGoodsDimensionSkus(id);
        if(dsList!=null&&!dsList.isEmpty()){
            List<DimensionSkuResult> list=new ArrayList<>();
            //以DimensionId分组
            Map<Integer, List<DimensionSkuVo>> collect = dsList.stream().collect(Collectors.groupingBy(DimensionSkuVo::getDimensionId));

            //将分组后的结果添加到一个新集合中
            for (Map.Entry<Integer, List<DimensionSkuVo>> entry : collect.entrySet()) {
                Integer dimensionId = entry.getKey();
                String dimensionName = entry.getValue().get(0).getDimensionName();
                DimensionSkuResult dsr=new DimensionSkuResult();
                dsr.setDimensionId(dimensionId);
                dsr.setDimensionName(dimensionName);
                dsr.setSkus(entry.getValue());
                list.add(dsr);
            }
           gdv.setDimensionSkus(list);
        }
        return gdv;
    }

    @Override
    public boolean checkGoodsHasSku(String id) {
        //是否有缓存,有则从缓存中取
        String key=RedisKeyConstant.GOODS_DETAIL_KEY + id;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            //取出缓存数据
            GoodsDetailVo gdv = JSONUtil.toBean(redisTemplate.opsForValue().get(key), GoodsDetailVo.class);
            return gdv.getDimensionSkus() != null;
        }else {
            //没有缓存,查询数据库
            GoodsDetailVo gdv = getGoodsDetailVo(id);
            //存入缓存
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(gdv),RedisKeyConstant.GOODS_DETAIL_EXPIRE, TimeUnit.MINUTES);
            return gdv.getDimensionSkus() != null;
        }
    }

    @Override
    public Integer getGoodsBalanceById(String id) {
           return   getById(id).getBalance();
    }

    @Transactional
    @Override
    public void reduceBalanceById(Map<String,Integer> goodsMap) {
        for (Map.Entry<String, Integer> e : goodsMap.entrySet()) {
            String goodsId = e.getKey();
            Integer count = e.getValue();
            UpdateWrapper<Goods> uw=new UpdateWrapper<>();
            uw.setSql("balance=balance-"+count).eq("id",goodsId);
            update(uw);
        }
    }

    @Override
    public List<GuessSearch> guessSearch() {
        LambdaQueryWrapper<Goods> lqw=new LambdaQueryWrapper<>();
        //搜索名字和id
        lqw.select(Goods::getName,Goods::getId);
        lqw.eq(Goods::getStatus,GoodsAndCategoryStatus.BEGIN_TO_SELL);
        List<Goods> list = list(lqw);
      return  BeanUtil.copyToList(list, GuessSearch.class);
    }

    @Override
    public List<GoodsSearch> search(String keyWord) {
        LambdaQueryWrapper<Goods> lqw=new LambdaQueryWrapper<>();
        lqw.like(Goods::getName,keyWord);
        List<Goods> list = list(lqw);
        return BeanUtil.copyToList(list, GoodsSearch.class);
    }

    @Override
    public Result getGoodsList() {
        List<Goods> list = list(Wrappers.<Goods>lambdaQuery().eq(Goods::getStatus, GoodsAndCategoryStatus.BEGIN_TO_SELL));
        return Result.success(list);
    }
}

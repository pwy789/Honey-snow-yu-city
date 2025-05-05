package com.pwy.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.GoodsAndCategoryStatus;
import com.pwy.common.RedisKeyConstant;
import com.pwy.entity.pojo.GoodsCategory;
import com.pwy.mapper.GoodsCategoryMapper;
import com.pwy.service.GoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class GoodsCategoryServiceImpl extends ServiceImpl<GoodsCategoryMapper, GoodsCategory>  implements GoodsCategoryService {
   @Autowired
   private StringRedisTemplate redisTemplate;

    @Override
    public List<GoodsCategory> getCategoryList() {
        //先查询redis
        String key = RedisKeyConstant.GOODS_CATEGORY_KEY;
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        if(listOperations.size(key)>0){
            List<String> results = listOperations.range(key, 0, listOperations.size(key));
          return results.stream().map(item-> JSONUtil.toBean(JSONUtil.toJsonStr(item),GoodsCategory.class)).collect(Collectors.toList());
        }
        //查询数据库
        List<GoodsCategory> list = list(Wrappers.<GoodsCategory>lambdaQuery().orderByAsc(GoodsCategory::getSort).eq(GoodsCategory::getStatus, GoodsAndCategoryStatus.BEGIN_TO_SELL));
        //存入redis
        listOperations.rightPushAll(key, list.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList()));
        //设置过期时间
        redisTemplate.expire(key, RedisKeyConstant.GOODS_CATEGORY_KEY_EXPIRE, TimeUnit.MINUTES);
       return list;
    }
}

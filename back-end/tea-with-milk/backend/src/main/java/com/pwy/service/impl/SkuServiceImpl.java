package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.Sku;
import com.pwy.mapper.SkuMapper;
import com.pwy.service.SkuService;
import org.springframework.stereotype.Service;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
    //根据dimensionId删除其所有的sku
    @Override
    public void deleteSkusByDimensionId(Integer id) {
        LambdaQueryWrapper<Sku> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Sku::getDimensionId,id);
        remove(lqw);
    }
}

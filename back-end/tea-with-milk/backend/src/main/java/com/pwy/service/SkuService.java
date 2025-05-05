package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.pojo.Sku;



public interface SkuService extends IService<Sku> {
    void deleteSkusByDimensionId(Integer id);
}

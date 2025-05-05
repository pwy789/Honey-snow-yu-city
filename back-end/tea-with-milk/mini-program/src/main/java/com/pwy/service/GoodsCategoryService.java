package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.pojo.GoodsCategory;

import java.util.List;

public interface GoodsCategoryService extends IService<GoodsCategory> {
    List<GoodsCategory> getCategoryList();
}

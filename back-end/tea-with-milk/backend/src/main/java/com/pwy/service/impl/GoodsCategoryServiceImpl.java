package com.pwy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.GoodsCategory;
import com.pwy.mapper.GoodsCategoryMapper;
import com.pwy.service.GoodsCategoryService;
import org.springframework.stereotype.Service;


@Service
public class GoodsCategoryServiceImpl extends ServiceImpl<GoodsCategoryMapper, GoodsCategory> implements GoodsCategoryService {

}

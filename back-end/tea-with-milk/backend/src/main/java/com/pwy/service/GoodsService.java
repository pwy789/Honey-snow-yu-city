package com.pwy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.pwy.common.Result;
import com.pwy.entity.dto.GoodsInfoDto;
import com.pwy.entity.dto.GoodsSearchDto;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.pojo.GoodsPictures;
import com.pwy.entity.vo.GoodsVo;


import java.util.List;

public interface GoodsService extends IService<Goods>{

    Page<Goods> getGoodsList(GoodsSearchDto dto);

    GoodsVo getGoodsById(String id);

    void updateGoods(GoodsInfoDto dto);


    void saveCoverImage(GoodsPictures gp);

    void add(Goods goods);

    Result delGoodsByIds(List<String> ids);


}

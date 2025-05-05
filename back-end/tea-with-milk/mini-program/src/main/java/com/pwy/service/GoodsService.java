package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.vo.GoodsDetailVo;
import com.pwy.entity.vo.GoodsSearch;
import com.pwy.entity.vo.GuessSearch;

import java.util.List;
import java.util.Map;

public interface GoodsService extends IService<Goods> {
    GoodsDetailVo getGoodsDetailById(String id);

    boolean checkGoodsHasSku(String id);
    //根据id查询商品,获取商品库存
    Integer getGoodsBalanceById(String id);

    //根据id扣减库存
    void reduceBalanceById(Map<String,Integer> map);

    List<GuessSearch> guessSearch();

    List<GoodsSearch> search(String keyWord);

    Result getGoodsList();
}

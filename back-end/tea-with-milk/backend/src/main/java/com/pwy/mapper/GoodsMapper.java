package com.pwy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.pwy.entity.pojo.Goods;
import com.pwy.entity.pojo.GoodsPictures;
import com.pwy.entity.pojo.GoodsSku;

import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GoodsMapper extends BaseMapper<Goods> {



    List<GoodsPictures> getGoodsImages(String id);

    List<GoodsSku> getGoodsSkus(String id);



    void insertGoodsImages(GoodsPictures gp);

    void updateGoodsImages(GoodsPictures gp);


    void saveImage(GoodsPictures gp);

    void deleteGoodsImages(Integer id);

    void insertGoodsSkus(GoodsSku goodsSku);

    void deleteGoodsSkus(Integer skuId);

    void delImageByIds(List<String> ids);

    void delSkusByIds(List<String> ids);

    void updateGoodsSkus(Integer price,Integer skuId);
}

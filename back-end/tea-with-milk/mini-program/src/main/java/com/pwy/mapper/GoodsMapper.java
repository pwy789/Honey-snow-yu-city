package com.pwy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.vo.DimensionSkuVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GoodsMapper extends BaseMapper<Goods> {
    List<String> getGoodsPictures(String id);

    List<DimensionSkuVo> getGoodsDimensionSkus(String id);

    void restoreBalance(@Param("map") Map<String, Integer> goodsMap);
}

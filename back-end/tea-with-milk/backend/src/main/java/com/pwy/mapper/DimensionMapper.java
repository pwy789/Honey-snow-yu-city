package com.pwy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pwy.entity.pojo.Dimension;
import com.pwy.entity.vo.DimensionVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DimensionMapper extends BaseMapper<Dimension> {
    List<DimensionVo> getAll();


    void deleteSkus(List<Integer> skuIds);
}

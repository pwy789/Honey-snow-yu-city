package com.pwy.service.impl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.Dimension;
import com.pwy.entity.pojo.Sku;
import com.pwy.entity.vo.DimensionVo;
import com.pwy.mapper.DimensionMapper;
import com.pwy.service.DimensionService;
import com.pwy.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DimensionServiceImpl extends ServiceImpl<DimensionMapper, Dimension> implements DimensionService {
    @Autowired
    private DimensionMapper dimensionMapper;
    @Autowired
    private SkuService skuService;
    @Override
    public List<DimensionVo> getAll() {
        return dimensionMapper.getAll();
    }

    @Transactional
    @Override
    public Boolean delById(Integer id) {
        try {
            //根据维度id先查询出它的sku,
            List<Integer> skuIds = skuService.list(Wrappers.<Sku>query().eq("dimension_id", id)).stream().map(Sku::getId).collect(Collectors.toList());
            //删除维度
            removeById(id);
            //删除sku
            skuService.deleteSkusByDimensionId(id);
            //goods_sku表删除对应的skus
            if(!skuIds.isEmpty()){
                dimensionMapper.deleteSkus(skuIds);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

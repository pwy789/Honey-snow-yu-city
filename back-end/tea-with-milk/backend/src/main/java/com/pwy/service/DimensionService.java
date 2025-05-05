package com.pwy.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.pojo.Dimension;
import com.pwy.entity.vo.DimensionVo;

import java.util.List;

public interface DimensionService extends IService<Dimension> {
    List<DimensionVo> getAll();

    Boolean delById(Integer id);
}

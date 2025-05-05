package com.pwy.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoodsStatistics {
    private String goodsId;

    private String goodsName;

    private Integer goodsCount;
}

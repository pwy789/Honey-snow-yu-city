package com.pwy.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageOrdersVo {
    private List<OrdersVo> ov;

    private long total;
}

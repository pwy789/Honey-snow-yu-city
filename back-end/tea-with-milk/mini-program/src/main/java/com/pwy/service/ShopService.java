package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.pojo.Shop;

public interface ShopService extends IService<Shop> {
    void addShop(Shop shop);
}

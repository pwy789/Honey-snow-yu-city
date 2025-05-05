package com.pwy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.Shop;
import com.pwy.mapper.ShopMapper;
import com.pwy.service.ShopService;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {
    @Override
    public void addShop(Shop shop) {
        save(shop);
    }
}

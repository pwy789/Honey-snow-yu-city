package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.dto.SetPasswordDto;
import com.pwy.entity.dto.ShopDto;
import com.pwy.entity.pojo.Shop;

public interface ShopService extends IService<Shop> {
    Result getShopInfo();

    Result updateShopInfo(ShopDto dto);

    Result updateShopPassword(SetPasswordDto dto);
}

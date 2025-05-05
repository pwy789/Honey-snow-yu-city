package com.pwy.controller;

import cn.hutool.core.bean.BeanUtil;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Shop;
import com.pwy.entity.vo.ShopVo;
import com.pwy.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;
    //新增店铺
    @PostMapping("/create")
    public Result addShop(@RequestBody Shop shop){
        shopService.addShop(shop);
       return  Result.success();
    }
    //获取店铺列表数据
    @GetMapping("/list")
    public Result getShopList(){
        List<Shop> list = shopService.list();
        List<ShopVo> shopVos = BeanUtil.copyToList(list, ShopVo.class);
        return  Result.success(shopVos);
    }
}

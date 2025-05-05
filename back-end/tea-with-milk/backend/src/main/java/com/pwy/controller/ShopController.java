package com.pwy.controller;

import com.pwy.common.Result;
import com.pwy.entity.dto.SetPasswordDto;
import com.pwy.entity.dto.ShopDto;
import com.pwy.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/shop")
@RestController
public class ShopController {
    @Autowired
    private ShopService shopService;
    //获取店铺信息
    @GetMapping("/info")
    public Result getShopInfo(){
        return shopService.getShopInfo();
    }
    //修改门店信息
    @PutMapping("/update")
    public Result updateShopInfo(@RequestBody ShopDto dto){
       return shopService.updateShopInfo(dto);
    }
    //修改店铺密码
    @PutMapping("/password")
    public Result updateShopPassword(@RequestBody SetPasswordDto dto){
       return shopService.updateShopPassword(dto);
    }
}


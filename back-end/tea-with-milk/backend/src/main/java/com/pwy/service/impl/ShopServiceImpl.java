package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.Result;
import com.pwy.entity.dto.SetPasswordDto;
import com.pwy.entity.dto.ShopDto;
import com.pwy.entity.pojo.Shop;
import com.pwy.entity.vo.EmployeeVo;
import com.pwy.mapper.ShopMapper;
import com.pwy.service.ShopService;
import com.pwy.utils.PasswordUtils;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {
    @Override
    public Result getShopInfo() {
        EmployeeVo ev = (EmployeeVo) ThreadLocalUtils.get();
        //店铺id
        String shopId = ev.getShopId();
        Shop shop = getById(shopId);
        shop.setPassword("******");
        return Result.success(shop);
    }

    @Override
    public Result updateShopInfo(ShopDto dto) {
        log.info("dto: {}",dto);
        String name = dto.getName();
        if(StringUtils.isBlank(name)){
            return Result.error("店铺名称不能为空");
        }
        String phoneRegex="1[3-9]\\d{9}";
        String phone = dto.getPhone();
        if(!phone.matches(phoneRegex)) return Result.error("请输入格式正确的手机号");
        String position = dto.getPosition();
        if(StringUtils.isBlank(position)) return  Result.error("店铺位置不能为空");
        String longitudeRegex="^-?((1[0-7]\\d|0?\\d{1,2})\\.\\d{6})$|^-?180\\.0{6}$";
        String latitudeRegex="^-?(([1-8]?\\d|0?\\d)\\.\\d{6})$|^-?90\\.0{6}$";
        String longitude = dto.getLongitude();
        String latitude = dto.getLatitude();
        if(!longitude.matches(longitudeRegex)||!latitude.matches(latitudeRegex)) return  Result.error("请输入格式正确的经纬度");
        //执行更新操作
        LambdaUpdateWrapper<Shop> luw=new LambdaUpdateWrapper<>();
        luw.set(Shop::getName,name);
        luw.set(Shop::getPhone,phone);
        luw.set(Shop::getLongitude,longitude);
        luw.set(Shop::getLatitude,latitude);
        luw.set(Shop::getPosition,position);
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        luw.eq(Shop::getId,ev.getShopId());
        update(luw);
        return Result.success("修改成功");
    }

    @Override
    public Result updateShopPassword(SetPasswordDto dto) {
        EmployeeVo ev = (EmployeeVo) ThreadLocalUtils.get();
        String shopId = ev.getShopId();
        //数据库中的密码
        String storePassword = getById(shopId).getPassword();
        String originalPassword = dto.getOriginalPassword();
        if(!PasswordUtils.verifyPassword(originalPassword,storePassword)) return  Result.error("原密码输入错误,修改失败!");
        //原密码输入正确,允许修改密码
        String hashSaltedNewPassword = PasswordUtils.getHashSaltedPassword(dto.getNewPassword());
        LambdaUpdateWrapper<Shop> luw=new LambdaUpdateWrapper<>();
        luw.set(Shop::getPassword,hashSaltedNewPassword);
        luw.eq(Shop::getId,shopId);
        update(luw);
        return Result.success("修改成功");
    }
}

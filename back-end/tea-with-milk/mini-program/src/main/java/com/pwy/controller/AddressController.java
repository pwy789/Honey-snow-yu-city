package com.pwy.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Address;
import com.pwy.service.AddressService;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    //获取收货地址
    @GetMapping("/list")
    public Result getAddress(){
        Object userId = ThreadLocalUtils.get();
        List<Address> list = addressService.list(Wrappers.<Address>lambdaQuery().eq(Address::getUserId, userId));
        return Result.success(list);

    }
    //新增收货地址
    @PostMapping("/add")
    public Result addAddress(@RequestBody Address address){
        Object userId = ThreadLocalUtils.get();
        address.setUserId(userId.toString());
        addressService.save(address);
        return  Result.success();
    }
    //删除收货地址
    @DeleteMapping("/delete/{id}")
    public Result deleteAddressById(@PathVariable String id){
        addressService.removeById(id);
        return Result.success("删除成功!");
    }
    //修改收货地址
    @PutMapping("/edit")
    public Result editAddress(@RequestBody Address address){
        Object userId = ThreadLocalUtils.get();
        address.setUserId(userId.toString());
        addressService.updateById(address);
        return Result.success("修改成功");
    }


}

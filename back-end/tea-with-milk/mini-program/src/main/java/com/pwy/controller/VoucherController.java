package com.pwy.controller;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Voucher;
import com.pwy.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    //根据id查询商品
   @GetMapping("/{id}")
    public Result getVoucherById(@PathVariable String id){
      return Result.success( voucherService.getById(id));
   }
   //获取优惠券列表
   @GetMapping("/list")
    public Result getVoucherList(){
       List<Voucher> voucherList = voucherService.getVoucherList();
       return  Result.success(voucherList);
   }
   //兑换优惠券
    @PostMapping("/exchange/{id}")
    public Result exchangeVoucher(@PathVariable String id){
      return  voucherService.exchange(id);
    }
}

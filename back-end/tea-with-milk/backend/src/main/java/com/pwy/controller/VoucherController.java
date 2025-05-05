package com.pwy.controller;

import com.pwy.common.Result;
import com.pwy.entity.dto.VoucherSearchDto;
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
    //新增优惠券
    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher){


        return    voucherService.addVoucher(voucher);
    }
    //获取优惠券
    @GetMapping("/list")
    public  Result getVoucherList(){
        List<Voucher> vouchers = voucherService.list();
        return Result.success(vouchers);
    }
    //修改优惠券
    @PutMapping
    public Result editVoucher(@RequestBody Voucher voucher){
        log.info("voucher: {}",voucher);
       return   voucherService.editVoucher(voucher);
    }
    //按条件查询优惠券
    @PostMapping("/search")
    public Result searchByCondition(@RequestBody VoucherSearchDto dto){
        List<Voucher> list = voucherService.search(dto);
        return Result.success(list);
    }

}

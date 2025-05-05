package com.pwy.controller;
import com.pwy.common.Result;
import com.pwy.entity.vo.VoucherVo;
import com.pwy.service.UserVoucherService;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/userVoucher")
@RestController
public class UserVoucherController {
    @Autowired
    private UserVoucherService userVoucherService;


    //获取用户优惠券列表
    @GetMapping("/list")
    public Result getVoucherList(){
        String userId = ThreadLocalUtils.get().toString();

        return   userVoucherService.getVoucherList(userId);

    }

}

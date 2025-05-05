package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.pojo.UserVoucher;
import com.pwy.entity.vo.VoucherVo;
import com.pwy.enums.VoucherStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface UserVoucherService extends IService<UserVoucher> {
    Result getVoucherList(String userId);

    //根据id删除优惠券
   void changeVoucherStatusById(String id, VoucherStatusEnum voucherStatusEnum, LocalDateTime time);
}

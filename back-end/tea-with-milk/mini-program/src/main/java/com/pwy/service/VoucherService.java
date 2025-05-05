package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.pojo.User;
import com.pwy.entity.pojo.Voucher;
import com.pwy.entity.vo.VoucherListVo;

import java.util.List;

public interface VoucherService extends IService<Voucher> {
   List<Voucher> getVoucherList();

    Result exchange(String id);

    Result operate(String id);
}

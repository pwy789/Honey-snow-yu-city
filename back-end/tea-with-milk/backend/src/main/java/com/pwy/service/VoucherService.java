package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.dto.VoucherSearchDto;
import com.pwy.entity.pojo.Voucher;

import java.util.List;

public interface VoucherService extends IService<Voucher> {
    Result addVoucher(Voucher voucher);

    Result editVoucher(Voucher voucher);

    List<Voucher> search(VoucherSearchDto dto);
}

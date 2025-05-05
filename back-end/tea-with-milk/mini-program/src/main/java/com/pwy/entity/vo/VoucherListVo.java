package com.pwy.entity.vo;

import com.pwy.entity.pojo.Voucher;
import lombok.Data;

import java.util.List;

@Data
public class VoucherListVo {
    private List<Voucher> voucherList;

    private Integer yuKingCoin;
}

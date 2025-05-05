package com.pwy.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class VoucherStatistics {
    private String voucherName;

    private Integer voucherUsedCount;
}

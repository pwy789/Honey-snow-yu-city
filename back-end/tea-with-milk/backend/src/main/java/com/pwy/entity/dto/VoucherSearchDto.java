package com.pwy.entity.dto;

import lombok.Data;

@Data
public class VoucherSearchDto {
    private String name;

    private String startTime;

    private String endTime;

    private String type;
}

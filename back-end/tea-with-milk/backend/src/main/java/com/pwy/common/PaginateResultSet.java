package com.pwy.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用于返回分页结果
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginateResultSet<T> {
    private T records;
    //数据量
    private Long total;
}

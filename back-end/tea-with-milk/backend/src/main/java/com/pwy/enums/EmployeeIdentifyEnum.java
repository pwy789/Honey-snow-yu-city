package com.pwy.enums;

import lombok.Getter;

@Getter
public enum EmployeeIdentifyEnum {
    ADMIN("管理员",0),
    ORDINARY_EMPLOYEE("普通员工",1 );

    private final String identify;

    private final Integer code;
    EmployeeIdentifyEnum(String identify,Integer code){
          this.identify=identify;

          this.code=code;
    }
}

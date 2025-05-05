package com.pwy.entity.pojo;

import lombok.Data;

//店铺实体类
@Data
public class Shop {
    private String id;

    private String name;

    private String longitude;

    private String latitude;

    private String position;
    //账号
    private String account;
    //密码
    private String password;

    private String phone;

    private String logo;
}

package com.pwy.entity.pojo;

import lombok.Data;

//小程序用户
@Data
public class User {
    private String id;

    private String nickname;

    private String phone;

    private String avatar;

    private Integer yuKingCoin;

    private Short gender;
}

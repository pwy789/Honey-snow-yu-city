package com.pwy.entity.dto;

import lombok.Data;
//接受用户修改个人信息的数据
@Data
public class UserDto {
    private String name;

    private String phone;

    private Short gender;

    private String avatarUrl;
}

package com.pwy.entity.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private String shopAccount;

    private String shopPassword;

    private String username;

    private String userPassword;
}

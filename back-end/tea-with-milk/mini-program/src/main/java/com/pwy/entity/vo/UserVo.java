package com.pwy.entity.vo;

import lombok.Data;
//返回给前端的用户信息
@Data
public class UserVo {
    private String nickname;

    private String phone;

    private String avatar;

    private Integer yuKingCoin;

    private Long voucherNum;

    private String token;

    private Short gender;
}

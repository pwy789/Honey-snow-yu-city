package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.dto.PhoneDto;
import com.pwy.entity.dto.UserDto;
import com.pwy.entity.pojo.User;

public interface UserService extends IService<User> {
    User toLogin(String phone, String code);

    void saveUserInfo(UserDto dto);

    Result confirmChangePhone(PhoneDto dto);

    Result createCode(String phone);
}

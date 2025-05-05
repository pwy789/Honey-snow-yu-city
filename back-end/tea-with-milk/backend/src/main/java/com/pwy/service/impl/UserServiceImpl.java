package com.pwy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.User;
import com.pwy.mapper.UserMapper;
import com.pwy.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}

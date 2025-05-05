package com.pwy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.UserVoucher;
import com.pwy.mapper.UserVoucherMapper;
import com.pwy.service.UserVoucherService;
import org.springframework.stereotype.Service;

@Service
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher> implements UserVoucherService {
}

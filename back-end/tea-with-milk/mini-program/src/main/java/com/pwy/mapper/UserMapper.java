package com.pwy.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pwy.entity.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}

package com.pwy.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pwy.common.RedisKeyConstant;
import com.pwy.common.Result;
import com.pwy.entity.dto.PhoneDto;
import com.pwy.entity.dto.UserDto;
import com.pwy.entity.pojo.User;
import com.pwy.entity.pojo.UserVoucher;
import com.pwy.entity.vo.UserVo;
import com.pwy.service.UserService;
import com.pwy.service.UserVoucherService;
import com.pwy.utils.JWTUtils;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserVoucherService userVoucherService;
    //登录
    @GetMapping("/login")
    public Result userLogin(String phone,String code){
        User user = userService.toLogin(phone, code);
        //生成token
        Map<String,Object> map=new HashMap<>();
        map.put("userId",user.getId());
        String token = JWTUtils.createToken(map);
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        userVo.setToken(token);
        //查询该用户拥有的优惠券的数量
        long num = userVoucherService.count(Wrappers.<UserVoucher>lambdaQuery().eq(UserVoucher::getUserId, user.getId()));
        userVo.setVoucherNum(num);
        return Result.success(userVo);
    }
    //获取用户信息
    @GetMapping("/info")
    public Result getUserInfo(){
        String userId = ThreadLocalUtils.get().toString();
        //查询该用户拥有的优惠券的数量
        long num = userVoucherService.count(Wrappers.<UserVoucher>lambdaQuery().eq(UserVoucher::getUserId, userId));
        User user = userService.getById(userId);
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        userVo.setVoucherNum(num);
        return Result.success(userVo);
    }
    //保存用户修改信息
    @PutMapping("/info")
    public Result saveUserInfo(@RequestBody UserDto dto){
        userService.saveUserInfo(dto);
        return Result.success("更新成功");
    }
    //生成验证码
    @GetMapping("/code")
    public Result createCode(String phone){
        return  userService.createCode(phone);

    }
    //确认修改手机号
    @PutMapping("/phone")
    public Result confirmChangePhone(@RequestBody PhoneDto dto){
       return userService.confirmChangePhone(dto);
    }
}

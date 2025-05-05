package com.pwy.service.impl;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.RedisKeyConstant;
import com.pwy.common.Result;
import com.pwy.constant.WeiXinLoginProperties;
import com.pwy.entity.dto.PhoneDto;
import com.pwy.entity.dto.UserDto;
import com.pwy.entity.pojo.User;
import com.pwy.mapper.UserMapper;
import com.pwy.service.UserService;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WeiXinLoginProperties wxLoginProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String phoneRegex="^1[3-9]\\d{9}$";

    private final String codeRegex="^\\d{4,6}$";
    @Override
    public User toLogin(String phone, String code) {

        Map<String,String> map=new HashMap<>();
        map.put("app_id",wxLoginProperties.getAppId());
        map.put("app_secret",wxLoginProperties.getAppSecret());
        map.put("js_code",code);
        map.put("grant_type",wxLoginProperties.getGrant_type());
        ResponseEntity<String> response = restTemplate.exchange(wxLoginProperties.getUrl() +
                "?appid={app_id}&" +
                "secret={app_secret}&" +
                "js_code={js_code}&" +
                "grant_type={grant_type}", HttpMethod.GET, null, String.class, map);
        String jsCode2sessionRes = response.getBody();
        JSONObject jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(jsCode2sessionRes));
        //获取openId
        String openId = jsonObject.getStr("openid");
        User user = getById(openId);

        if(user==null){
            //说明没用该用户,则创建
            User newUser=new User();
            newUser.setId(openId);
            int length = RandomUtil.randomInt(4, 7);
            // 生成随机昵称
            String randomNickname = RandomUtil.randomString(length);
            newUser.setNickname("用户"+randomNickname);
            newUser.setPhone(phone);
            newUser.setYuKingCoin(0);
            //保存
            save(newUser);
            return newUser;
        }
        //说明已存在该用户
        return user;
    }

    @Override
    public void saveUserInfo(UserDto dto) {
        String userId = ThreadLocalUtils.get().toString();
        LambdaUpdateWrapper<User> luw=new LambdaUpdateWrapper<>();
        luw.set(StringUtils.isNotBlank(dto.getAvatarUrl()),User::getAvatar,dto.getAvatarUrl());
        luw.set(StringUtils.isNotBlank(dto.getGender().toString()),User::getGender,dto.getGender());
        luw.set(StringUtils.isNotBlank(dto.getPhone()),User::getPhone,dto.getPhone());
        luw.set(User::getNickname,dto.getName());
        luw.eq(User::getId,userId);
        update(luw);
    }

    @Override
    public Result confirmChangePhone(PhoneDto dto) {
        String phone = dto.getPhone();
        String code = dto.getCode();
        if (!phone.matches(phoneRegex)) return Result.error("请输入正确的手机号");

        if (!code.matches(codeRegex)) return Result.error("请输入格式正确的验证码");
        String value = redisTemplate.opsForValue().get(RedisKeyConstant.USER_PHONE_CODE_KEY + phone);
        if (value == null || !value.equals(code)) {
            return Result.error("验证码错误");
        }
        //修改用户的手机号
        String userId = ThreadLocalUtils.get().toString();
        if (update(Wrappers.<User>lambdaUpdate().set(User::getPhone,phone).eq(User::getId,userId))){
            //删除这个key
            redisTemplate.delete(RedisKeyConstant.USER_PHONE_CODE_KEY+userId);
            return Result.success("修改成功");
        }
        return Result.error("修改失败,请稍后重试");
    }
    @Override
    public Result createCode(String phone) {
        if (!phone.matches(phoneRegex)) {
            return Result.error("请填写正确的手机号");
        }
        //生成4-6位的验证码
        int length = RandomUtil.randomInt(4, 7);
        String code = RandomUtil.randomNumbers(length);
        log.info("code: {}",code);
        redisTemplate.opsForValue().set(RedisKeyConstant.USER_PHONE_CODE_KEY+phone, code,RedisKeyConstant.USER_PHONE_CODE_TTL, TimeUnit.SECONDS);
        return Result.success();
    }
}

package com.pwy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.Result;
import com.pwy.entity.dto.UserLoginDto;
import com.pwy.entity.pojo.Employee;
import com.pwy.entity.pojo.Shop;
import com.pwy.entity.vo.EmployeeVo;
import com.pwy.enums.ResponseEnum;
import com.pwy.mapper.EmployeeMapper;
import com.pwy.service.EmployeeService;
import com.pwy.service.ShopService;
import com.pwy.utils.JWTUtils;
import com.pwy.utils.PasswordUtils;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private ShopService shopService;
    @Override
    public Result login(UserLoginDto dto) {
       log.info("加盐密码: {}",PasswordUtils.getHashSaltedPassword(dto.getUserPassword()));
        //查询出店铺
        String shopAccount = dto.getShopAccount();
        Shop shop = shopService.getOne(Wrappers.<Shop>lambdaQuery().eq(Shop::getAccount, shopAccount));
        if(shop==null){
            return Result.error("店铺不存在");
        }

        if(!PasswordUtils.verifyPassword(dto.getShopPassword(),shop.getPassword())){
            return Result.error("店铺密码错误");
        }
        String username = dto.getUsername();
        Employee employee = getOne(Wrappers.<Employee>lambdaQuery().eq(Employee::getUsername, username));
        if(employee==null){
            return Result.error("该用户不存在");
        }
        if(!employee.getShopId().equals(shop.getId())){
            return Result.error("该用户不属于该店铺");
        }
        if(!PasswordUtils.verifyPassword(dto.getUserPassword(),employee.getPassword())){
            return Result.error("用户密码错误");
        }
        //信息无误
        EmployeeVo ev = BeanUtil.copyProperties(employee, EmployeeVo.class);
        Map<String, Object> employeeInfo = new HashMap<>(BeanUtil.beanToMap(ev));
        String token = JWTUtils.createToken(employeeInfo);
        ev.setToken(token);
        return Result.success(ev, ResponseEnum.LOGIN_SUCCESS);
    }

    @Override
    public Result addEmployee(Employee employee) {
         //首先检查密码是否输入合理,大于6位即可
        String password = employee.getPassword();
        if(password.length()<6){
            return Result.error("密码长度必须大于6位");
        }
        //检查姓名是否为空
        String name = employee.getName();
        if(StringUtils.isBlank(name)){
            return Result.error("姓名不能为空");
        }
        //检查手机号是否格式正确
        String phoneRegex="1[3-9]\\d{9}";
        String phone = employee.getPhone();
        if(!phone.matches(phoneRegex)){
            return Result.error("请输入正确的手机号格式");
        }
        //检查用户名是否为空或者已经存在
        String username = employee.getUsername();
        if(StringUtils.isBlank(username)){
            return Result.error("用户名不能为空");
        }
        long count = count(Wrappers.<Employee>lambdaQuery().eq(Employee::getUsername, username));
        if(count>0){
            return Result.error("该用户名已经存在!");
        }
        //创建用户
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        String shopId = ev.getShopId();
        //将密码加密
        String hashSaltedPassword = PasswordUtils.getHashSaltedPassword(password);
        employee.setPassword(hashSaltedPassword);
        employee.setShopId(shopId);
        save(employee);
        return Result.success("添加成功");
    }
}

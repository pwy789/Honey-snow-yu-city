package com.pwy.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pwy.common.Result;
import com.pwy.entity.dto.EmployeeDto;
import com.pwy.entity.dto.SetPasswordDto;
import com.pwy.entity.dto.UserLoginDto;

import com.pwy.entity.pojo.Employee;
import com.pwy.entity.vo.EmployeeVo;
import com.pwy.enums.EmployeeIdentifyEnum;
import com.pwy.service.EmployeeService;
import com.pwy.utils.PasswordUtils;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDto dto){

        return    employeeService.login(dto);
    }
    //获取店铺下的员工
    @GetMapping("/list")
    public Result getEmployeeList(){
        EmployeeVo ev = (EmployeeVo) ThreadLocalUtils.get();
        String shopId = ev.getShopId();
        List<Employee> empList = employeeService.
                list(Wrappers.<Employee>lambdaQuery().eq(Employee::getShopId, shopId).
                        eq(Employee::getIdentify, EmployeeIdentifyEnum.ORDINARY_EMPLOYEE.getCode()));
        for (Employee employee : empList) {
            employee.setPassword("");
        }
        return Result.success(empList);
    }
    //获得个人信息
    @GetMapping
    public Result getPersonInfo(){
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        Employee employee = employeeService.getById(ev.getId());
        EmployeeVo result = BeanUtil.copyProperties(employee, EmployeeVo.class);
        return Result.success(result);
    }
    //修改个人信息
    @PutMapping
    public Result updatePersonInfo(@RequestBody EmployeeDto dto){
        if(StrUtil.isBlank(dto.getName())){
            return Result.error("员工姓名不能为空");
        }
        String phoneRegex="1[3-9]\\d{9}";
        if(!dto.getPhone().matches(phoneRegex)){
            return Result.error("请输入格式正确的手机号");
        }
        LambdaUpdateWrapper<Employee> luw=new LambdaUpdateWrapper<>();
        luw.set(Employee::getName,dto.getName());
        luw.set(Employee::getPhone,dto.getPhone());
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        String id = ev.getId();
        luw.eq(Employee::getId,id);
        employeeService.update(luw);
        return Result.success("修改成功");
    }
    //修改密码
    @PutMapping("/password")
    public Result UpdatePassword(@RequestBody SetPasswordDto dto){
        EmployeeVo ev = (EmployeeVo) ThreadLocalUtils.get();
        String employeeId = ev.getId();
        Employee employee = employeeService.getById(employeeId);
        //数据库中的密码
        String storePassword = employee.getPassword();
        String originalPassword = dto.getOriginalPassword();
        if(!PasswordUtils.verifyPassword(originalPassword,storePassword)){
            return Result.error("原密码输入错误,修改失败!");
        }
        String newPassword = PasswordUtils.getHashSaltedPassword(dto.getNewPassword());
        //原密码输入正确,允许修改密码
        LambdaUpdateWrapper<Employee> luw=new LambdaUpdateWrapper<>();
        luw.set(Employee::getPassword,newPassword);
        luw.eq(Employee::getId,employee.getId());
        employeeService.update(luw);
        return Result.success("修改成功");
    }
    //添加员工
    @PostMapping("/add")
    public Result addEmployee(@RequestBody Employee employee){
      return   employeeService.addEmployee(employee);
    }
    //根据id删除员工
    @DeleteMapping("/del/{id}")
    public Result delEmployeeById(@PathVariable String id){
        employeeService.removeById(id);
        return Result.success("删除成功");
    }
}

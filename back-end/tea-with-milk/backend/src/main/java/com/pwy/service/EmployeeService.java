package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.dto.UserLoginDto;
import com.pwy.entity.pojo.Employee;

public interface EmployeeService extends IService<Employee> {
    Result login(UserLoginDto dto);

    Result addEmployee(Employee employee);
}

package com.pwy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pwy.entity.pojo.Employee;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeMapper extends BaseMapper<Employee> {
}

import type { EmployeeLoginForm } from "@/types/Employee";
import http from "@/utils/http";

export const employeeLoginAPI=(data:EmployeeLoginForm)=>{
  return http({
    url:'/employee/login',
    method:'POST',
    data
  })
}
//获取店铺下的员工
export const getEmployeeListAPI=()=>{
  return http({
    url:'/employee/list',
    method:'GET',
    
  })
}
//获取个人信息
export const getPrsonInfoAPI=()=>{
  return http({
    url:'/employee'
  })
}
//修改个人基本信息
export const setPersonInfoAPI=(data:{name:string,phone:string})=>{
  return http({
    url:'/employee',
    method:'PUT',
    data
  })
}
//修改密码
export const setPasswordAPI=(data:{originalPassword:string,newPassword:string,})=>{
  return http({
    url:'/employee/password',
    method:'PUT',
    data
  })
}
//添加员工
export const addEmployeeAPI=(data:{username:string,password:string,name:string,phone:string})=>{
    return http({
      url:'/employee/add',
      method:'POST',
      data
    })
}
//根据id删除员工
export const delEmployeeByIdAPI=(id:string)=>{
  return http({
    url:`/employee/del/${id}`,
    method:'DELETE',
  })
}
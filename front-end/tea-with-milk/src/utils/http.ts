import axios from 'axios';
import { ElMessage,ElLoading } from 'element-plus';
import { useEmployeeStore } from '@/stores/Employee';

import router from '@/router';

const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' },
 
});
let loadInstance:any
// 添加请求拦截器
http.interceptors.request.use(function (config) {
  loadInstance=  ElLoading.service({
    lock: true,
    text: '加载中',
    background: '#FFFFFF',
  });
  const  employeeStore=useEmployeeStore()
  if(config.url?.includes('/login')) return config
  if(employeeStore.employeeInfo?.token){
    config.headers.Authorization=employeeStore.employeeInfo.token
  }else{
    ElMessage.error('身份验证过期,请重新登录')
     router.replace('/')
     
  }
  // 在发送请求之前做些什么
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});

// 添加响应拦截器
http.interceptors.response.use(function (response) {
  loadInstance.close()
  // 2xx 范围内的状态码都会触发该函数。
  // 对响应数据做点什么
  if(response.data.code>=200&&response.data.code<300){
    if(response.data.message){
      ElMessage.success(response.data.message)
    }
  }else{
    ElMessage.error(response.data.message)
  }
 
 
  return response.data;
}, function (error) {
    const {response} = error
    if(response&&response.status===401){
      ElMessage.error('身份验证过期,请重新登录')
      router.replace('/')
      
    }
    if(response&&response.status===403){
      ElMessage.error('您没有改执行权限')
    }
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  return Promise.reject(error);
});
export default http
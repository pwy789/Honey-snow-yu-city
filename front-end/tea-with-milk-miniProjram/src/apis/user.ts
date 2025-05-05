import type { user, UserCart } from "@/types/user"
import { http } from "@/utils/http"
//用户登录
export const userLoginAPI=(phone:string,code:string)=>{
    return http<user>({
      url:`/user/login?phone=${phone}&code=${code}`,
      
    })
}
//将商品加入购物车
export const addToCartAPI=(data:{id?:string,goodsId?:string,skuInfo?:string,count:number,price?:number})=>{
  return http({
    url:'/cart',
    method:'POST',
    data
  })

}
//获取购物车数据
export const getCartListAPI=()=>{
  return http<UserCart[]>({
    url:'/cart/list'
  })
}
//清空购物车
export const clearCartListAPI=()=>{
  return http({
    url:'/cart/clear',
    method:'DELETE'
  })
}
//获取用户的个人信息
export const getUserInfoAPI=()=>{
  return http<user>({
    url:'/user/info'
  })
}
//保存用户信息
export const saveUserInfoAPI=(data:{name:string,phone:string,gender:number,avatarUrl:string})=>{
  return http({
    url:'/user/info',
    method:'PUT',
    data
  })
}
//获取验证码
export const getCodeAPI=(phone:string)=>{
  return http({
    url:`/user/code?phone=${phone}`,
    
  })
} 
//确认修改手机号
export const confirmChangePhoneAPI=(data:{code:string,phone:string})=>{
  return http({
    url:'/user/phone',
    method:'PUT',
    data
  })
}
import type { ShopInfo } from "@/types/Shop"
import http from "@/utils/http"

export const getShopInfoAPI=()=>{
  return http<ShopInfo>({
    url:'/shop/info',
    method:'GET'
  })
}
//修改门店信息
export const updateShopInfoAPI=(data:{name:string,longitude:number,latitude:number,position:string,phone:string})=>{
  return http({
    url:'/shop/update',
    method:'PUT',
    data
  })

}
//修改店铺密码
export const updateShopPasswordAPI=(data:{originalPassword:string,newPassword:string})=>{
  return http({
    url:'/shop/password',
    method:'PUT',
    data
  })
}
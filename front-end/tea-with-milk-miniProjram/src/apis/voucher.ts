import type { voucher, voucherData } from "@/types/Voucher"
import { http } from "@/utils/http"

//获取用户拥有的优惠券列表
export const getUserVoucherListAPI=()=>{
  return http<voucherData[]>({
    url:'/userVoucher/list'
  })
}
//获取优惠券列表
export const getVoucherListAPI=()=>{
  return http<voucher[]>({
    url:'/voucher/list'
    
  })
}
//兑换优惠券
export const exchangeVoucherAPI=(id:string)=>{
  return http({
    url:`/voucher/exchange/${id}`,
    method:'POST'
  })
}
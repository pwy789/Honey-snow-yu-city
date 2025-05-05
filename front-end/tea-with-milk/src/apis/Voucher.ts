import type { Voucher } from "@/types/Voucher"
import http from "@/utils/http"

export const getVoucherListAPI=()=>{
  return http({
    url:'/voucher/list',
    method:'GET'
  })
}
//添加优惠券
export const addVoucherAPI=(data:Voucher)=>{
  return http({
    url:'/voucher',
    method:'POST',
    data
  })
}
//修改优惠券
export const editVoucherAPI=(data:Voucher)=>{
  return http({
    url:'/voucher',
    method:'PUT',
    data
  })
}
//条件查询优惠券
export const getVoucherListByConditionAPI=(data:{name:string,startTime:string,endTime:string,type:string})=>{
  return http({
    url:'/voucher/search',
    method:'POST',
    data
  })
}
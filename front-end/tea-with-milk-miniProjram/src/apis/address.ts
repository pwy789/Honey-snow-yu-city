import type { Address } from "@/types/Address"
import { http } from "@/utils/http"
//获取地址列表
export const getAddressListAPI=()=>{
  return http<Address[]>({
    url:'/address/list'
  })
}
//根据id删除地址
export const deleteAddressByIdAPI=(id:string)=>{
  return http({
    url:`/address/delete/${id}`,
    method:'DELETE',
    
  })
}
//编辑
export const editAddressAPI=(data:Address)=>{
  return http({
    url:'/address/edit',
    method:'PUT',
    data
  })
}
//新增收货地址
export const addAddressAPI=(data:Address)=>{
  return http({
    url:'/address/add',
    method:'POST',
    data
  })
}
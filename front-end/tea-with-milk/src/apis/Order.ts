import http from "@/utils/http"

export const getOrdersByIdAPI=(id:string)=>{
  return http({
    url:`/order/${id}`
  })
}
//获取该店铺的订单列表
export const getOrdersListAPI=(status?:number)=>{
  return http({
    url:'/order/list',
    params:{
    status
    }
  })
}
//按条件查询订单
export const searchOrdersAPI=(form:{orderNum?:string,beginTime:string,endTime:string,status:string,page:number,pageSize:number})=>{
  return http({
    url:'/order/search',
    data:form,
    method:'POST'
  })
}
//完成订单
export const finishOrderAPI=(id:string)=>{
  return http({
    url:`/order/finish/${id}`,
    method:'POST'
  })
}
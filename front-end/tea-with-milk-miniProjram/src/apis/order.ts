import type { orderInfoToBack, Orders, OrdersByStatus } from "@/types/Order"
import { http } from "@/utils/http"

//生成订单
export const createOrderAPI=(data:orderInfoToBack)=>{
   return http({
    url:'/order/createorder',
    method:'POST',
    data
   })
}
//根据id获取订单信息
export const getOrderInfoAPI=(id:string)=>{
   return http<Orders>({
    url:`/order/${id}`
   })
}
//设置订单状态
export const cancelOrderAPI=(orderId:string)=>{
   return http({
    url:`/order/cancel/${orderId}`,
    method:'PUT',
   })
}
//获取不同状态的订单
export const getOrderListByStatusAPI=(data:{page:number,pageSize:number,status:number[]})=>{
   return http<OrdersByStatus>({
      url:'/order/status',
      data,
      method:'POST'
   })
}
//确认支付
export const confirmPayAPI=(orderId:string)=>{
   return http({
      url:`/order/pay/${orderId}`,
      method:'PUT'
   })
}
//获取订单配送费与打包费
export const getOrderFeeAPI=(shopId:string,addressId:string,shopNum:number)=>{
        return http<{deliveryFee:number,packagingFee:number}>({
          url:'/order/fee',
          data:{shopId,addressId,shopNum},
          method:'POST'
        })
}
//再来一单
export const oneMoreOrderAPI=(orderId:string)=>{
 return http({
   url:`/order/oneMore/${orderId}`  
 })
}
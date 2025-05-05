import type { UserCart } from "./user";
import type { voucher } from "./Voucher";

//传递给后端的订单信息
export interface orderInfoToBack{
  //订单商品数据
   goods:UserCart[],
   //总价
   totalPrice:number,
   //如果是外送的话，需要传递地址
   address?:string,
   //外送需要传递联系人
   name?:string,
   //外送需要传递电话
   phone?:string,
   gender:number,
   //备注
   remark?:string
   //店铺id
   shopId:string,
   //店铺名称
   shopName:string
   //购买方式为外送时,需要传递配送费以及打包费
   deliveryFee?:number,
   packagingFee?:number,
   voucherId:string
}
//订单与包含商品类型
export type Orders={

  orders:{
    id:string,
    name:string,
    phone:string,
    address:string,
    totalPrice:number,
    status:number,
    createTime:string,
    remark:string,
    gender:number,
    userVoucherId?:string,
    deliveryFee:number,
    packagingFee:number,
    shopName:string,
    orderTime:string,
    orderId?:string
  }
  ogs:OrdersGoods[],
  voucher?:voucher
 

}
type OrdersGoods={
  goodsId:string,
  goodsName:string,
  coverImage:string,
  count:number,
  price:number,
  skuInfo:string,
}
//指定状态下的订单结果集
export type OrdersByStatus={
  ovs:Orders[],
  count:number
}
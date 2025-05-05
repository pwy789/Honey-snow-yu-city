export type orders={
  id:string,
  orderId:string,
  remark:string,
  address?:string,
  name:string,
  phone:string,
  gender:string,
  createTime:string,
  totalPrice:number,
  deliveryFee?:number,
  packagingFee?:number,
  orderTime:string,
  voucherName:string,
  status:string
  ogv:OrdersGoods[],

}
interface OrdersGoods{
   goodsName:string,
   price:number,
   coverImage:string,
   count:number,
   skuInfo:string
}
export type PageOrders={
  ov:orders[],
  total:number
}
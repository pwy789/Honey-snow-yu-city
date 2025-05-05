//接收后端传来的优惠券数据
export interface voucherData{
   id:string
   voucher:voucher
   status:number
   acquireTime:string,
   validityPeriod:number,
   endTime?:string
}
export type voucher={
  id:string,
  name:string,
  url:string,
  discountRate:number,
  threshold:number,
  deduct:number,
  effectiveTime:string,
  expirationTime:string,
  type:number,
  coinNeed:number
  //库存
  number:number
}

//优惠券类型
export type Voucher={
  id?:string,
  name:string,
  url:string,
  discountRate?:string,
  threshold?:string,
  deduct?:string,
  effectiveTime:Date,
  expirationTime?:Date,
  number:number,
  type:string,
  coinNeed:number,
  status?:string
}
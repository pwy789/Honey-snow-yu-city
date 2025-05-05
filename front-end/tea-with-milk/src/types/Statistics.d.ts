export type Statistics={
  goodsStatistics:GoodsStatistics[],
  orderCountByDay:DayCount[],
  ordersStatistics:OrdersStatistics[],
  voucherStatistics:VoucherStatistics[]
}
export interface OrdersStatistics{
    orderCount:number,
    orderTotalPrice:number,
    orderWay:number
} 
export interface VoucherStatistics{
  voucherName:string,
  voucherUsedCount:number
}
export interface GoodsStatistics{
  goodsCount:number,
  goodsId:number,
  goodsName:string
}
export interface DayCount{
  day:string,
  count:number
}
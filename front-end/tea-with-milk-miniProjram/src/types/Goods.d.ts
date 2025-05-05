//商品类型
export type Goods={
  id:string,
  name:string,
  categoryId:string,
  price:string,
  introduction:string
  description:string,
  balance:string,
  status:number,
  isChecked?:boolean,
  coverImage?:string,
  // 购买数量
  number?:number
}
interface skus{
  skuId:number,
  skuPrice:number,
  skuName:string,
  dimensionId:number,
  dimensionName:string,
  dimensionSort:number,
  isChecked?:boolean
}
//维度与sku的对应关系
interface DimensionSkus{

  dimensionId:number,
  dimensionName:string,
  skus: skus[]
}
//商品详情
export interface GoodsDetail extends Goods{
  images:string[],
  dimensionSkus?:DimensionSkus[]

} 
//猜你喜欢类型
interface guessLike{
  id:string,
  coverImage:string,
  name:string,
  price:number
}
//商品搜索类型
export type GoodsSearch={
  id:string,
  coverImage:string,
  name:string,
  price:number,
  introduction:string
}

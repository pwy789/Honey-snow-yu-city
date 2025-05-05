
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
  coverImage?:string
}
//商品图片类型
export type GoodsPictures={
 id?:number,
  url:string,
  goodsId:string,
  
 
}
type GoodsSku={
id:number,
goodsId:string,
skuId:number,
price:number
}
//添加商品所需的字段类型
interface GoodsInfo extends Goods{ 
  images:GoodsPictures[],
  skus?:GoodsSku[],
  skuOperate?:{ skuId: number; operate: 'add' | 'delete'|'update' }[]
}
//商品条件查询类型
export type GoodsQuery={
  name:string,
  categoryId:string,
  priceLow:string,
  priceHigh:string,
  status:string,
  page:number,
  pageSize:number
}
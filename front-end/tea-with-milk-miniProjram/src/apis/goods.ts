import { http } from "@/utils/http";
import type {GoodsCategory} from "@/types/GoodsCategory";
import type { Goods, GoodsDetail, GoodsSearch, guessLike } from "@/types/Goods";
//获取商品分类数据
export const getGoodsCategoryAPI=()=>{
 return http<GoodsCategory[]>({
  url:'/goodscategory/list'
 })
}
//获取商品数据集合
export const getGoodsListAPI=()=>{
 return http<Goods[]>({
  url:'/goods/list',
  method:'GET',
 })
}
//根据id获取商品详情
export const getGoodsDetailAPI=(id:string)=>{
  return http<GoodsDetail>({
    url:`/goods?id=${id}`
  })
}
//检查商品是否包含sku
export const checkGoodsHasSkuAPI=(id:string)=>{
  return http<boolean>({
    url:`/goods/check/${id}`
  })
}
//获取猜你喜欢数据

export const getGuessYouLikeAPI=()=>{
  return http<guessLike[]>({
    url:'/goods/guesslike'
  })
}

//获取猜你想搜数据
export const getGuessSearchAPI=()=>{
  return http<{id:string,name:string}[]>({
    url:'/goods/guesssearch'
  })
}
//搜索商品
export const searchGoodsAPI=(keyWord:string)=>{
 return http<GoodsSearch[]>({
  url:`/goods/search?keyWord=${keyWord}`
 })
}
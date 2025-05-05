import type { Goods, GoodsInfo, GoodsQuery } from "@/types/Goods";
import http from "@/utils/http";

//获取商品数据
export const getGoodsListAPI=(condition:GoodsQuery)=>{
  return http({
    url:'/goods/list',
    method:'POST',
    data:condition

  })
}

// 添加商品
export const addGoodsAPI=(data:Goods)=>{
  return http({
    url:'/goods/add',
    method:'POST',
    data

  })
}
//根据id查询商品
export const getGoodsByIdAPI=(id:string)=>{
  return http({
    url:`/goods/${id}`,
    method:'GET',
  })
}
//修改商品
export const updateGoodsAPI=(data:GoodsInfo&{imageOperate:{id?:number, url: string; operation: 'add' | 'update' | 'delete'}[]})=>{
  return http({
    url:'/goods/update',
    method:'PUT',
    data
  })
}
//删除已选商品
export const delGoodsISCheckedAPI=(idStr:string)=>{
  return http({
    url:`/goods/delete?${idStr}`,
    method:'DELETE',
  })
}
//修改商品状态
export const setGoodsStatusAPI=(id:string,status:number)=>{
  return http({
    url:`/goods/status/${id}/${status}`,
    method:'PUT',
  })
}
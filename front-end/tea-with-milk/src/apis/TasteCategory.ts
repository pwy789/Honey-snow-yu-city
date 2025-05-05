import http  from "@/utils/http";
import type { TasteCategory } from "@/types/TasteCategory";
//添加商品分类
export const addCategoryAPI=(data:TasteCategory)=>{
  return http({
    url:'/goodscategory/add',
    method:'POST',
    data
  })
}
//获取所有商品分类
export const getGoodsCategoryListAPI=()=>{
  return http({
    url:'/goodscategory/list',
    method:'GET'
  })
}

//修改商品分类状态
export const setCategoryStatusAPI=(id:string,status:number)=>{
  return http({
    url:`/goodscategory/status/${id}`,
    method:'PUT',
    params:{
      status

    }
  })
}
//修改商品分类
export const updateCategoryAPI=(data:TasteCategory)=>{
  return http({
    url:'/goodscategory/update',
    method:'PUT',
    data
  })
}
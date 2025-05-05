import http from "@/utils/http";
//获取所有的分类及其sku
export const getDimensionSkusAPI=()=>{
  return http({
    url:'/dimension/getall'
  })
}
//删除某个sku
export const delSkuByIdAPI=(id:number)=>{
  return http({
    url:`/sku/${id}`,
    method:'DELETE',
  })
}
//添加主类
export const addMainAPI=(data:{name:string,sort:number})=>{
   return http({
    url:'/dimension/add',
    method:'POST',
    data
   })
}
//修改主类
export const updateMainAPI=(data:{id:number,name:string,sort:number})=>{
  return http({
    url:'/dimension/update',
    method:'PUT',
    data
  })
}
//删除主类
export const delMainAPI=(id:number)=>{
  return http({
    url:`/dimension/delete/${id}`,
    method:'DELETE',
   
  })
}
//添加sku需要的数据类型
interface skuInfo{
  name:string,
  price?:number,
  dimensionId:number
}
//添加sku
export const addSkuAPI=(data:skuInfo)=>{
 return http({
  url:'/sku/add',
  method:'POST',
  data
 })
}
//修改sku
export const updateSkuAPI=(data:{id:number,name:string})=>{
 return http({
  url:'/sku/update',
  method:'PUT',
  data
 })
}

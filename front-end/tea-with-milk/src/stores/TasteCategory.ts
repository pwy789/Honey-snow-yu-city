import type { TasteCategory } from "@/types/TasteCategory";
import { defineStore } from "pinia";
import { ref } from "vue";

export default defineStore('TasteCategory',()=>{
  //商品分类数据
  const TasteCategoryList=ref<TasteCategory[]>([])

  const setTasteCategoryListService=(data:TasteCategory[])=>{
    TasteCategoryList.value=data
  }
  const removeTasteCategoryListService=()=>{
    TasteCategoryList.value=[]
  }
  const clearTasteCategoryListService=()=>{
    TasteCategoryList.value=[]
    localStorage.removeItem('TasteCategory')
  }
  return {
    TasteCategoryList,
    setTasteCategoryListService,
    removeTasteCategoryListService,
    clearTasteCategoryListService
  }
},
{
  persist:true
})
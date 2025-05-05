import { defineStore } from "pinia";
import type {ShopType}  from "@/types/Shop"
import { ref } from "vue";
export const useShopStore=defineStore('shop',()=>{
  const shopList=ref<ShopType[]>([])
  //用户选中的店铺
  const shopActive=ref<ShopType>({} as ShopType)
  const setShopList=(data:ShopType[])=>{
    shopList.value=data
  }
  const setShopActive=(data:ShopType)=>{
    shopActive.value=data
  }
  const clearShopData=()=>{
    shopList.value=[]
    shopActive.value={} as ShopType
  }
  return {
    shopList,
    setShopList,
    shopActive,
    setShopActive,
    clearShopData
  }
},
{
  persist: {
    storage: {
      getItem(key) {
        return uni.getStorageSync(key)
      },
      setItem(key, value) {
        uni.setStorageSync(key, value)
      },
    },
  },
})
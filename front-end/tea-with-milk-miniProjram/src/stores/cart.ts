import type { buyNow, UserCart } from "@/types/user";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useCartStore=defineStore('cart',()=>{
    const cartList=ref<UserCart[]>([])
    
      //用户立即购买的商品
  const buyNowGoods=ref<UserCart>()
    const setCartList=(data:UserCart[])=>{
     cartList.value=data
    }
    //清空购物车
    const clearCartList=()=>{
      cartList.value=[]
      
    }
  return {
    cartList,
    setCartList,
    clearCartList,
    buyNowGoods
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
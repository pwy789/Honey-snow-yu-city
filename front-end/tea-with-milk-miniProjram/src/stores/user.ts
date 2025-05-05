import type { user } from "@/types/user";
import { defineStore } from "pinia";
import { ref } from "vue";
export const useUserStore=defineStore('user',()=>{
  //存储用户的信息
  const userInfo=ref<user>({} as user)
  //用户购买方式 pick-up或者delivery
 let buyWay=ref('pick-up')
  
  //设置用户信息
  const setUserInfo=(data:any)=>{
    userInfo.value=data
  }
  //清理用户信息
  const clearUserInfo=()=>{
    userInfo.value={} as user
    
  }
  //设置购买方式
  const setBuyWay=(data:string)=>{
    buyWay.value=data
  }
  return {
    userInfo,
    setUserInfo,
    clearUserInfo,
    buyWay,
    setBuyWay
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
},)
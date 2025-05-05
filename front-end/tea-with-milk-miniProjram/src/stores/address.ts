import type { Address } from "@/types/Address";
import { defineStore } from "pinia";
import { ref } from "vue";
export const useAddressStore=defineStore('address',()=>{
  const addressData=ref<Address[]>([])
  //用户选中的地址
  const addressActive=ref<Address>({} as Address)

  const setAddressActive=(data:Address)=>{
    addressActive.value=data
  }
  const setAddressData=(data:Address[])=>{
    addressData.value=data
  }
  const clearAddressData=()=>{
    addressData.value=[]
    addressActive.value={} as Address
  }
  return {
    addressData,
    setAddressData,
    addressActive,
    setAddressActive,
    clearAddressData
  }
},{
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
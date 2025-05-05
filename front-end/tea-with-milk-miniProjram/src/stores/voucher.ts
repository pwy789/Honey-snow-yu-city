import type { voucherData } from "@/types/Voucher";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useVoucherStore=defineStore('voucher',()=>{
  //优惠券列表集合
  const voucherList=ref<voucherData[]>([])
  //用户选中的优惠券
  const voucherActive=ref<voucherData>({} as voucherData)
  const setVoucherList=(data:voucherData[])=>{
    voucherList.value=data
  }
  const setVoucherActive=(data:voucherData)=>{
    voucherActive.value=data
  }
  const clearVoucherList=()=>{
    voucherList.value=[]
  }
  const clearVoucherActive=()=>{
    voucherActive.value={} as voucherData
  }
  return {
    voucherList,
    voucherActive,
    setVoucherList,
    setVoucherActive,
    clearVoucherList,
    clearVoucherActive
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
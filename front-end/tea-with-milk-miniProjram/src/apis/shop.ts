import type { ShopType } from "@/types/Shop"
import { http } from "@/utils/http"

export const getShopListAPI=()=>{
  return http<ShopType[]>({
    url:'/shop/list'
  })
}
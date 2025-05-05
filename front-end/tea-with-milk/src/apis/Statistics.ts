import type { Statistics } from "@/types/Statistics"
import http from "@/utils/http"

//查询今日订单数据
export const getcurDataAPI=(date:string)=>{
  return http<Statistics>({
    url:`/statistics/today?date=${date}`
  }
  )
}
//查询本周订单数据
export const getcurWeekDataAPI=(date:string)=>{
  return http<Statistics>({
    url:`/statistics/week?date=${date}`
  })
}
//查询本月订单数据
export const getcurMonthDataAPI=(date:string)=>{
  return http<Statistics>({
    url:`/statistics/month?date=${date}`
  })
}
//查询本年订单数据
export const getcurYearDataAPI=(year:string)=>{
  return http<Statistics>({
    url:`/statistics/year?year=${year}`
  })
}
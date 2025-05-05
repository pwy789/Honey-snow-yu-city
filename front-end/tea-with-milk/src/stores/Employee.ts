import type { EmployeeInfo } from "@/types/Employee";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useEmployeeStore = defineStore('employee', () => {
  const employeeInfo = ref<EmployeeInfo>()

  const setEmployeeInfo = (data: EmployeeInfo) => {
    employeeInfo.value = data
  }
  const clearEmployeeInfo=()=>{
    employeeInfo.value={} as EmployeeInfo
    localStorage.removeItem('employee')
  }
  return { employeeInfo,setEmployeeInfo,clearEmployeeInfo }
},
{
  persist: true
})
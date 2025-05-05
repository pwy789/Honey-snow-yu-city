<script lang="ts" setup>
import {ref} from 'vue';
import { useEmployeeStore } from '@/stores/Employee';
import TasteCategory from '@/stores/TasteCategory';
import {useRouter} from 'vue-router'
const router=useRouter();
const employeeStore=useEmployeeStore();
const tasteStore=TasteCategory();
const employeeInfo=ref(employeeStore.employeeInfo);
//退出登录
const logout=()=>{
//清除pinia数据以及本地缓存
employeeStore.clearEmployeeInfo()
tasteStore.clearTasteCategoryListService()
//回到登录页
router.replace('/')
}
</script>
<template>
<div class="navbar">
  <p>立 志 超 越 蜜 雪 冰 城</p> 
  <div class="name">
    <h4>{{employeeInfo?.name}}</h4>
   <h4>{{ employeeInfo?.phone }}</h4>
   <el-popconfirm title="您确认退出登录吗?" @confirm="logout">
    <template #reference>
      <el-icon :size="20" color="#FF0000"><SwitchButton /></el-icon>
    </template>
  </el-popconfirm>


  </div>
 
</div>

</template>
<style lang="less" scoped>
.navbar{
  padding: 0 100px;
  display: flex;
  justify-content: space-between;
  height: 50px;
  align-items: center;
 border: 1px solid #ccc;
 border-radius: 5px; 
 background-color: #f9f9f9;
 box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
 transition: background-color 0.3s ease; /* 添加过渡效果 */
 &:hover {
    background-color: #e9e9e9; /* 鼠标悬停时的背景颜色 */
  }
   .name{

    display: flex;
    justify-content: space-around;
    align-items: center;
    min-width: 120px;
   
  h4{
    margin: 0 15px;
    font-size: 16px;
    color: #333;
  }
//  退出登录
  svg{
      cursor: pointer;
  }
   }
}
.dialog{
  z-index: 999;
  position: fixed;
  top: 50px;
  right:70px;
  opacity: 0;
}
.showDialog{
  width: 120px;
  height: 100px;
  opacity: 1;
}
</style>

<script lang="ts" setup>
import { ref } from 'vue'
const isCollapse = ref(false)
import {
  Menu as IconMenu,
  Tickets,
} from '@element-plus/icons-vue'
const defaultActive=ref('/statistics')
</script>

<template>
  <div class="sidebar">
    <div class="logo">
   <img src="@/assets/logo.png" >
   <p>蜜 雪 宇 城</p>
    </div>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#545c64"
        class="el-menu-vertical-demo"
        :default-active="defaultActive"
        text-color="#fff"
        :collapse="isCollapse"
        :router="true"
      >
      <el-menu-item index="/statistics">
        <el-icon><Calendar /></el-icon>
          <span>流水统计</span>
        </el-menu-item>
       <el-sub-menu index="2">
        <template #title>
          <el-icon><icon-menu /></el-icon>
          <span>分类管理</span>
        </template>
        <el-menu-item-group >
          <el-menu-item index="/tastecategory">
          
            <span>款式分类</span>
          </el-menu-item >
          <el-menu-item index="/skuCategory">
            <span>sku分类</span>
          </el-menu-item >
      </el-menu-item-group>
       </el-sub-menu>
     
        <el-menu-item index="/milkytea">
          <el-icon><MilkTea /></el-icon>
          
          <span>商品中心</span>
        </el-menu-item>
        <el-menu-item index="/shop" >
          <el-icon><Shop /></el-icon>
          <span>门店信息</span>
        </el-menu-item>
        <el-menu-item index="/voucher">
          <el-icon><Ticket /></el-icon>
          <span>优惠券管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
            <el-icon><Tickets /></el-icon>
          <span>订单明细</span>
        </el-menu-item>
        
      </el-menu>
    </div>
 
</template>


<style scoped lang="less">
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;

  
}

.el-menu-item{
  height: 65px;
}
.logo{
  display: flex;
  flex-direction: column;
  width: 200px;
  height: 200px;
   background-color: #F5F7FA;
  img{
    border: none;
    margin: 10px auto;
    width: 65%;
    height: 65%;
    border-radius: 50%;
    border: 1px solid #1F1F1F;
  }
  img:before {
            content: '未找到图片';
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
            background-color: #f0f0f0;
            color: #999;
        }
 p{
  text-align: center;
  flex: 1;
  font-family: 'Times New Roman', serif;
    color: #8B4513;
    text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
    font-size: 24px;

 }
}
</style>
<script lang="ts" setup>

import Sidebar from './components/Sidebar.vue';
import Navbar from './components/Navbar.vue';


</script>

<template>
 <div class="layout">
  <div class="left">
    <Sidebar/>
  </div>
   <div class="right">
    <Navbar />
    <router-view/>
   </div>
  
    
  </div>

</template>


<style scoped lang="less">
.layout{
  height: 100%;
  display: flex;
  .right{
    flex: 1;
  }
}


</style>
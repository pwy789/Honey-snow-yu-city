<script lang="ts" setup>
import { ref } from 'vue';
const swiperIndex=ref(0)
defineProps<{
  swiperList:string[]
}>()
//轮播图的图片变化时触发
const swiperImgChange=(e:any)=>{
  swiperIndex.value=e.detail.current
}
</script>
<!-- 轮播图组件 -->
<template>
<swiper
  autoplay
  circular
  class="swiper"
  @change="swiperImgChange"
>
 <swiper-item v-for="item in swiperList" :key="item" >
  <image
    :src="item"
    mode="scaleToFill"
  />
 </swiper-item>

</swiper>
<!-- 轮播图指示点 -->
<view class="swiperPoint" v-if="swiperList.length>1">
  <view class="point" v-for="index in swiperList.length" :key="index" :class="{'swiperActive':swiperIndex===index-1}"/> 
</view>
</template>

<style lang="less">
// 轮播图
.swiper{
  height: 550rpx;
  image{
  width: 100%;
  height: 100%;
}
}
// 轮播图指示点
.swiperPoint{
  position: absolute;
  display: flex;
  height: 16rpx;
  left: 60rpx;
  top: 415rpx;
  .point{
    margin: 5rpx;
    background-color: #FFFFFF;
    width: 30rpx;
  }
  // 轮播图被选中
.swiperActive{
  background-color: #333;
}
}
</style>
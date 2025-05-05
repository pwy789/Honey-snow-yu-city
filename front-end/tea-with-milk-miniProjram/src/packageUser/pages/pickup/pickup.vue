<template>
  <!-- 地图区域 -->
<map show-location enable-3D :markers="markers" :longitude="userSelectedLocation.longitude" :latitude="userSelectedLocation.latitude"></map>
<!-- 店铺选择 -->
 <scroll-view scroll-y>
  <!-- 每一项 -->
  <view class="item" v-for="shop in shopList" :key="shop.id" @tap="selectShop(shop.id)">
    <view class="shopName">{{shop.name}}</view>
    <view class="status">
      <text>营业中</text>
      <text>可外送</text>
    </view>
    <view class="other">营业时间:全天</view>
    <view class="other">直线距离211m</view>
  </view>
 </scroll-view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getShopListAPI } from '@/apis/shop';
import type { ShopType } from '@/types/Shop';
import { useShopStore } from '@/stores/shop';
const shopStore=useShopStore()
const shopList=ref<ShopType[]>()
onLoad(async()=>{
  //或者店铺数据列表
const res= await getShopListAPI()
shopList.value=res.data
//存入pinia
shopStore.setShopList(res.data)

  uni.getFuzzyLocation({
    type: 'wgs84',
    success:async(res)=>{
      //经度
      userSelectedLocation.value.longitude=res.longitude
      //纬度
      userSelectedLocation.value.latitude=res.latitude
    }

  })
})
//用户选择的经纬度
const userSelectedLocation=ref({
  longitude:0,
  latitude:0
})

//店铺标记点位
const markers=ref([
  {
    id:1,
    longitude:113.082578,
    latitude:28.320408,
    iconPath:'/static/position.png',
    width:35,
    height:40,
    callout:{
      content:'蜜雪宇城(信息学院校外店)',
      bgColor:'#61605F',
      color:'#FBFBFA',
      borderRadius:10,
      display:'ALWAYS',
      padding:10
    }
  },
  {
    id:2,
    longitude:112.855911,
    latitude:28.213721,
    iconPath:'/static/position.png',
    width:35,
    height:40,
    callout:{
      content:'蜜雪冰城(和馨园商业街店)',
      bgColor:'#61605F',
      color:'#FBFBFA',
      borderRadius:10,
      display:'ALWAYS',
      padding:10
    }
  }
])
//选中店铺时触发
const selectShop=(shopId:string)=>{
 const shop= shopList.value!.find(s=>s.id===shopId)
 userSelectedLocation.value.longitude=shop!.longitude
 userSelectedLocation.value.latitude=shop!.latitude
 shopStore.setShopActive(shop!)
 uni.navigateBack()
}
</script>

<style lang="less">
page{
  background-color: #F8F8F8;
}
map{
  width: 100%;
  height: 550rpx;
  margin-bottom: 20rpx;
}
scroll-view{
  height: 640rpx;
  .item{
    border-radius: 20rpx;
    background-color: #FFFFFF;
    margin: 0 auto;
    width: 700rpx;
    padding: 20rpx;
   view{
    margin: 0 0 15rpx 0;
   }
   .shopName{
    font-size: 30rpx;
    font-weight: bold;

   }
   .status{
    text{
      margin-right: 10rpx;
      font-size: 25rpx;
      padding: 10rpx 20rpx;
      border-radius: 10rpx;
    }
    text:first-child{
      color: #FFFFFF;
      background-color: #E60012;
    }
    text:last-child{
      color: #E60012;
      background-color: #FFF8F8;
    }
   }
   .other{
    color: #777777;
    font-size: 26rpx;
   }
  }
}
</style>
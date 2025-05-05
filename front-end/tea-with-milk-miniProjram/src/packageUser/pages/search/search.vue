<script lang="ts" setup>
import {ref, watch} from 'vue'
import { onLoad } from '@dcloudio/uni-app';
import {getGuessSearchAPI,searchGoodsAPI} from '@/apis/goods'
import type { GoodsSearch } from '@/types/Goods';
//封装猜你想搜数据
const guessSearchList=ref<{id:string,name:string}[]>([])
//接收搜索得到的商品数据集
const searchGoodsList=ref<GoodsSearch[]>([])
//搜索框的值
const searchInp=ref('')
//控制热门推荐部分的显示与隐藏
const hotRecommendVisible=ref(true)
//获取猜你想搜数据
onLoad(async()=>{
  const res=await getGuessSearchAPI()
  guessSearchList.value=res.data
})

//跳转到商品详情页
const toGoodsDetail=(id:string)=>{
   uni.navigateTo({
    url:`/pages/shop/goodsDetail/goodsDetail?id=${id}`,
  }) 
}
//清除输入框的值
const clear=()=>{
  searchInp.value=''
}
//搜索
const search=async()=>{
  //如果用户没有输入任何内容,直接点击的搜索,那么查询placeholder的值
 if(!searchInp.value){
  uni.navigateTo({ url: `/pages/shop/goodsDetail/goodsDetail?id=${guessSearchList.value[0].id}` })
 } else{
 //隐藏热门推荐部分
 hotRecommendVisible.value=false 
 const res=await searchGoodsAPI(searchInp.value)
 searchGoodsList.value=res.data
 
 }

}
watch(searchInp,(newVal,oldVal)=>{
  if(oldVal.length>0&&newVal===''){
    hotRecommendVisible.value=true
  }
})
</script>
<template>
  <!-- 搜索页面 -->
  <view class="search">
    <!-- 左侧输入框 -->
    <view class="inp">
      <!-- 搜索图标 -->
       <view class="icon">  
        <uni-icons type="search" size="25" class="search-icon"></uni-icons>
      </view>
    
      <input type="text" :placeholder="guessSearchList[0].name" v-if="guessSearchList.length>0" v-model="searchInp">
      <!-- 清除按钮 -->
      
        <uni-icons type="clear" size="19" class="clear-icon" @tap="clear" v-show="searchInp.length>0"></uni-icons>
      
    

    </view>
    <!-- 右侧搜索 -->
    <view  @tap="search">搜索</view>
  </view>
  <!-- 热门推荐 -->
   <view class="hot-recommend" v-show="hotRecommendVisible">
    
      <text>热门</text><text>推荐</text>
      <image
        src="@/static/hot-recommend.png"
        mode="scaleToFill"
      />
     <!-- 商品块 -->
     <view class="goods-block">
     <view class="item" v-for="(item,index) in guessSearchList" :key="item.id" :class="{'very-hot':index<=1}" @tap="toGoodsDetail(item.id)">
      {{item.name}}   <image src="@/static/hot-goods.png" mode="scaleToFill" v-if="index<=1"/>
     </view>
     </view>
   </view>
   <!-- 搜索商品展示 -->
    <scroll-view class="search-goods" scroll-y v-show="!hotRecommendVisible">
     <!-- 每一项商品 -->
      <view class="item" v-if="searchGoodsList.length>0" v-for="item in searchGoodsList" :key="item.id" @tap="toGoodsDetail(item.id)">
        <!-- 左边图片区域 -->
        <view class="left">
          <image :src="item.coverImage" mode="scaleToFill"/>
        </view>
        <!-- 右边信息区域 -->
        <view class="right">
           <text class="name">{{ item.name }}</text>
           <text class="introduction">{{ item.introduction }}</text>
           <text class="price">￥{{ item.price }}</text>
        </view>
      </view>
    </scroll-view>
</template>



<style lang="less">
// 搜索区域
.search{
  display: flex;
  justify-content: space-around;
  align-items: center;
 height: 80rpx;
 
 .inp{
  display: flex;
   width: 80%;
   height: 100%;
   border-radius: 40rpx;
   background-color: #F1F1F2;
  
   .search-icon{
    display: block;
    margin: 16rpx 12rpx 0;
   }
   input{
 
    width: 500rpx;
    height: 100%;
    font-size: 25rpx;
    letter-spacing: 5rpx;
   }
   .clear-icon{
    flex: 1;
    margin-top: 20rpx;
    margin-right: 10rpx;
   }
 }

}
//热门推荐
.hot-recommend{
  padding: 20rpx;
  height: 400rpx;
  margin-top: 50rpx;
    position: relative;
    text:nth-child(2){
    color: red;
   
  }
  image{
      position: absolute;
      width: 40rpx;
      height: 40rpx;
    }
 .goods-block{
  flex-wrap: wrap;
  display: flex;
 
  .item{
    min-width: 160rpx;
    position: relative;
    margin: 20rpx;
    padding-left: 20rpx;
    padding-right: 20rpx;
    font-size: 25rpx;
    line-height: 80rpx;
    border-radius: 10rpx;
    height: 80rpx;
    background-color: #F3F3F3;
    image{
      display: inline-block;
      position: absolute;
      top: 25%;
      width: 35rpx;
      height: 35rpx;
      
    }
    &.very-hot{
      background-color:#FFF8F8 ;
      color: #FC3F41 ;
    }
  }
 }
}
//搜索商品展示
.search-goods{
  margin-top: 20rpx;
height: 900rpx;
 .item{
  margin-bottom: 20rpx;
  padding: 15rpx;
   display: flex;
   height: 200rpx;
   .left{
    margin-right: 25rpx;
    width: 200rpx;
    image{
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }
   }
   .right{
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    text{
      display: block;
    }
   }
   .name{
    font-weight: bold;
   }
   .introduction{
    color: #B2AAAA;
    font-size: 25rpx;
   }
   .price{
    font-weight: 500;
   }
 }
}
</style>
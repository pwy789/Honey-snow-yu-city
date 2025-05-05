<script lang="ts" setup>
import type { guessLike } from '@/types/Goods';

defineProps<{
  GoodsList:guessLike[]
}>()
//跳转到商品详情页
const getGoodsDetail=(goodsId:string)=>{
 uni.navigateTo({
   url:`/pages/shop/goodsDetail/goodsDetail?id=${goodsId}`
 })
}
</script>
<!-- 猜你喜欢组件 -->
<template>
  <view class="guess-like">
    <text class="title">猜你喜欢</text>
    <scroll-view scroll-x class="data" enable-flex>
      <!-- 每一项 -->
      <view class="item" v-for="item in GoodsList" :key="item.id" @tap="getGoodsDetail(item.id)">
        <image
          :src="item.coverImage"
          mode="scaleToFill"
        />
        <!-- 商品名称与购买按钮 -->
        <view class="name-btn">
         <text>{{item.name}}</text>
         <text class="price">￥{{item.price}}</text>
         <!-- +按钮 -->
         <view class="add-btn">+</view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>
<style lang="less">
 // 猜你喜欢
 .guess-like{
  padding: 25rpx;
  border-radius: 20rpx;
  height: 500rpx;
 
  background-color: #FFEBEF;
  .title{
    font-size: 35rpx;
    font-weight: bold;
    letter-spacing: 5rpx;
  }
  .data{
    display: flex;
   
    margin-top: 10rpx;
   height: 88%;
    flex-wrap: nowrap;
    
    .item{
      padding: 40rpx 20rpx 20rpx;
      margin-right: 30rpx;
      display: flex;
      flex-direction: column;
      align-items: center;
      border-radius: 20rpx;
      width: 42%;
      height: 100%;
      background-color: #FFFFFF;
      image{
        width: 200rpx;
        height: 200rpx;
        background-color: red;
      }
      .name-btn{
        position: relative;
        margin-top: 20rpx;
          width: 100%;
          flex: 1;
          text{
            margin: 20rpx 0;
          color: #4C4C4C;
          font-size: 28rpx;
           display: block;
          }
          .price{
            color: #333;
          }
          .add-btn{
            position: absolute;
            right: 0;
            bottom: 20rpx;
          text-align: center;
          line-height: 40rpx;
            width: 40rpx;
            height: 40rpx;
            border-radius: 50%;
            margin: 0 20rpx;
            background-color: #E60012;
            color: #FFFFFF;
     }
      }
    }
  }
 }
</style>
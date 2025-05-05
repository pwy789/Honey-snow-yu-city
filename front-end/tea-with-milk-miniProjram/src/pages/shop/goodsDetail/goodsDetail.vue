<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app';
import {getGoodsDetailAPI} from '@/apis/goods'
import SwiperComponent from '@/components/SwiperComponent.vue';
import { computed, ref } from 'vue';
import type { GoodsDetail } from '@/types/Goods';
import GoodsCount from '../components/GoodsCount.vue';
import { addToCartAPI } from '@/apis/user';
import { useUserStore } from '@/stores/user';
import { useCartStore } from '@/stores/cart';
const userStore=useUserStore()
const cartStore=useCartStore()
const props=defineProps<{
  id:string
}>()
//存储商品的所有数据
const GoodsDetailData=ref<GoodsDetail>({} as GoodsDetail)
onLoad(async()=>{
const res= await getGoodsDetailAPI(props.id)
GoodsDetailData.value=res.data
// 设初始量为1
GoodsDetailData.value.number=1
//设所有sku为未选择
GoodsDetailData.value.dimensionSkus?.forEach(item=>{
  //加料默认不选中
  if(item.dimensionName==='加料') {
    item.skus.forEach(g=>{
      g.isChecked=false
    })
    return
  }
  item.skus.forEach((g,index)=>{
    //默认选中第一项
    if(index===0){
      g.isChecked=true
    }else{
      g.isChecked=false
    }
  
  })
})

})
//增加商品数量
const add=()=>{
  GoodsDetailData.value!.number!++
}
//减少商品数量
const reduce=()=>{
  GoodsDetailData.value!.number!--
}
//加入购物车
const addToCart=async()=>{
  if(!userStore.userInfo!.token){
  return uni.showModal({
      title:'温馨提示',
      content:'请先登录',
      success:(res)=>{
     if(res.confirm){
      uni.navigateTo({
        url:'/packageUser/pages/login/login'
      })

     }
      }
    })
  }
  if(GoodsDetailData.value.number!<1){
   return uni.showToast({
      title:'商品数量不能小于1',
      icon:'none'
    })
  }
 await addToCartAPI({
  goodsId:GoodsDetailData.value.id,
  count:GoodsDetailData.value.number!,
  skuInfo:skuInfo.value,
  price:finalPrice.value
 })
 back()
}
//立即购买
const buyNow=()=>{
 
  cartStore.buyNowGoods={
    goodsId:GoodsDetailData.value.id,
    count:GoodsDetailData.value.number!,
    skuInfo:skuInfo.value,
    price:finalPrice.value,
    coverImage:GoodsDetailData.value.coverImage,
    goodsName:GoodsDetailData.value.name
  }
  uni.navigateTo({
    url:'/packageUser/pages/orderSettlement/orderSettlement?type=buyNow'
  })
  
}
//返回
const back=()=>{
  uni.navigateBack()
}
//选中了某个sku时触发
const selectedSku=(id:number,dimensionId:number)=>{
  //如果是加料
  if(GoodsDetailData.value.dimensionSkus?.find(v=>v.dimensionId===dimensionId)?.dimensionName==='加料'){
    GoodsDetailData.value.dimensionSkus!.find(v=>v.dimensionId===dimensionId)!.skus.forEach(j=>{
      if(j.skuId===id){
        j.isChecked=!j.isChecked
      }else{
        j.isChecked=false

      }
    })
  }else{
    //其他
    GoodsDetailData.value.dimensionSkus!.find(v=>v.dimensionId===dimensionId)!.skus.forEach(v=>{
  if(v.skuId===id){
    v.isChecked=true
  }else{
    v.isChecked=false
  }

})
  }

}
//此computed用于计算选中的sku
const skuInfo=computed(()=>{
  return GoodsDetailData.value.dimensionSkus?.map(v=>{
   return v.skus.find(v=>v.isChecked)?.skuName
  }).filter(Boolean).join("/")
})
//此computed用于计算选中sku后最终的价格
const finalPrice=computed(()=>{
  if (!GoodsDetailData.value.dimensionSkus) {
    return Number(GoodsDetailData.value.price);
  }
  return Number(GoodsDetailData.value.price) + GoodsDetailData.value.dimensionSkus.reduce((pre, cur) => {
    return pre + (cur.skus.find(v => v.isChecked)?.skuPrice || 0);
  }, 0);
})
</script>
<template>
 
    <scroll-view scroll-y class=scroll>
  <!-- 商品轮播图 -->
  <SwiperComponent :swiperList="GoodsDetailData!.images" v-if="GoodsDetailData?.id"/>
  
      <!-- 商品名称以及sku部分 -->
   <view class="sku" v-if="GoodsDetailData.dimensionSkus">
    <!-- 商品名称 -->
    <text class="name">{{ GoodsDetailData?.name }}</text>
    <!-- 每一项sku部分 -->
    <view class="item" v-for="item in GoodsDetailData?.dimensionSkus" :key="item.dimensionId">
      <!-- 左侧维度名 -->
      <view class="dimension">{{ item.dimensionName }}</view>
      <!-- 右侧维度所包含的sku -->
      <view class="skus" >
        <view class="sku" v-for="g in item.skus" :key="g.skuId" @tap="selectedSku(g.skuId,item.dimensionId)" :class="{'sku-active':g.isChecked}">
            <text>{{ g.skuName }}</text> <text v-if="g.skuPrice" class="price">￥{{ g.skuPrice }}</text> 
        </view>
      </view>
    </view>
   </view>
   <!-- 商品详情部分 -->
   <view v-if="GoodsDetailData?.description" class="desc">
   <text>商品详情</text>

   <view class="content">
    {{ GoodsDetailData.description }}
   </view>
   </view>
  </scroll-view>
  <!-- 返回按钮 -->
   <view class="back" @tap="back">
    <uni-icons type="left" size="20" color="#333333"></uni-icons>
   </view>
   <!-- 底部操作栏 -->
    <view class="action">
      <!-- 价格,选择sku与按钮区域 -->
      <view class="top">
        <view class="left">
          <text>￥</text> <text v-if="finalPrice">{{ finalPrice }}</text>
          <view v-if="skuInfo">{{skuInfo}}</view>
        </view>
        <!-- GoodsCount组件 -->
          <GoodsCount :goods-id="GoodsDetailData.id" 
          :goods-number="GoodsDetailData.number!" @add-to-cart="add" @reduce-to-cart="reduce"/>
      </view>
      <!-- 底部立即购买与加入购物车 -->
     <view class="bottom">
       <button @tap="buyNow">立即购买</button>
       <button @tap="addToCart">加入购物车</button>
     </view>
    </view>


</template>


<style lang="less">

// sku部分
.sku{
 padding: 40rpx 20rpx;
 .name{
  display: block;
  font-size: 35rpx;
  font-weight: bold;
  margin-bottom: 40rpx;
 }
 .item{
   display: flex;
   align-items: start;
   flex-wrap: wrap;
   .dimension{
    margin-top: 20rpx;
    text-align: center;
    font-size: 28rpx;
 width: 10%;
 height: 100%;
   font-weight: bold;
   }
   .skus{
    display: flex;
     flex-wrap: wrap;
    
     view{
      margin: 0;
      padding: 0;
      box-sizing: border-box;
     }
   flex: 1;
   .sku{
    font-size: 28rpx;
    color: #777777;
    padding-left: 20rpx;
    padding-right: 20rpx;
    border: 1px #E6E6E6 solid;
    height: 68rpx;
    line-height: 68rpx;
    min-width: 180rpx;
    border-radius: 10rpx;
    margin: 10rpx;
    .price{
      margin-left: 50rpx;
    }
    // sku被选中时触发
   }
   .sku-active{
      background-color: #FFF7F8;
      border: 1rpx solid #FFBFC6;
      color: #E60012;
    }
   }
 }
}
//商品详情
.desc{
 text{
  display: block;
  margin-bottom: 50rpx;
 }
  padding: 20rpx;
  border-top: 15rpx solid #F4F4F7; 
  border-bottom: 2rpx solid #F4F4F7;
  .content{
    white-space: pre-line;
    font-size: 25rpx;
    color: #AAAAAA;
  }
}
// 底部操作栏
.action{
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 220rpx;
  padding: 20rpx 10rpx 10rpx;
  .top{
     display: flex;
     justify-content: space-between;
    .left{    
    
      text:nth-child(1){
    font-weight: bold;
    font-size: 25rpx;
  }
  text:nth-child(2){
    font-size: 35rpx;
    font-weight: bold;
  }
  view{
    font-size: 25rpx;
    color: #777777;
  }
    }
   
    
  }
  .bottom{
    margin-top: 20rpx;
    display: flex;
    button{
      width: 300rpx;
    }
    button:nth-child(1){
      background-color: #FFFFFF;
      color: #333333;
    }
    button:nth-child(2){
      background-color: #E60012;
      color: #FFF7D9;
    }
  }
}
.scroll{
  height: 1100rpx;
 
}
// 返回按钮
.back{
  position: fixed;
  top: 80rpx;
  left: 30rpx;
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  background-color: #F1F1F1;
  text-align: center;
  line-height: 60rpx;
}
</style>
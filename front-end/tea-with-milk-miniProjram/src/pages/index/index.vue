<script lang="ts" setup>
import {ref} from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import {getGuessYouLikeAPI} from '@/apis/goods';
import SwiperComponent from '@/components/SwiperComponent.vue';
import { useUserStore } from '@/stores/user';
import GuessLike from '@/components/GuessLike.vue';
import type { guessLike } from '@/types/Goods';
const userStore=useUserStore()
const guessLikeData=ref<guessLike[]>([])
onLoad(async()=>{
 const res=await getGuessYouLikeAPI()
 guessLikeData.value=res.data
})
// 轮播图数据
const swiperList=[
  'http://192.168.204.133:9000/milky-tea/swiper/2025/04/02/8d45e9c9969444f398ebff814848971f.png',
  'http://192.168.204.133:9000/milky-tea/swiper/2025/04/02/8d9f3cdb0fe5406fa9538c38cbbacba8.png',
  'http://192.168.204.133:9000/milky-tea/swiper/2025/04/02/dc1f3f7ce18b449dbc9111233d81d60d.png',
  'http://192.168.204.133:9000/milky-tea/swiper/2025/04/02/e3b183df0555488d92bbc261921e47f5.png'
]
//点击授权登录时触发
const toLogin=()=>{
  uni.navigateTo({
    url:'/packageUser/pages/login/login'
  })
}
//购买方式
const buyWay=(way:'pick-up'|'delivery')=>{
  userStore.setBuyWay(way)
  uni.switchTab({
    url:`/pages/shop/shop`
  })
}
</script>
<template>
  <!-- 轮播图 -->
  <SwiperComponent :swiperList="swiperList"/>
 
<view class="container">
<!-- 会员信息 -->
<view class="member-info">
  <!-- 会员头像 -->
  <view class="avatar">
    <image
      :src="userStore.userInfo?.avatar"
      mode="scaleToFill"
      v-if="userStore.userInfo?.avatar"
    />
    <uni-icons type="person" size="50" v-else />
  </view>
  <!-- 会员基本信息 -->
  <view class="info" v-if="userStore.userInfo!.token">
    <view class="phone">{{userStore.userInfo!.phone.substring(0,3)}}*****{{ userStore.userInfo!.phone.substring(8) }}</view>
    <view class="nickname">{{userStore.userInfo!.nickname}}</view>
  </view>
  <!-- 会员其他信息 -->
  <view class="other" v-if="userStore.userInfo!.token">
    <!-- 宇王币 -->
    <view class="coin">
      <text class="count">{{userStore.userInfo!.yuKingCoin}}</text>
      <text class="field">宇王币</text>
    </view>
     <!-- 优惠券 -->
    <view class="coupon">
      <text class="count">{{userStore.userInfo.voucherNum}}</text>
      <text class="field">优惠券</text>
    </view>
  </view>
  <!-- 授权登录 -->
   <view class="toLogin" v-else @tap="toLogin">
    授权登录
   </view>
</view>
<!-- 购买方式 -->
 <view class="buy-way">
   <view class="pick-up" @tap="buyWay('pick-up')">
   <image
    src="@/static/pick-up.png"
    mode="scaleToFill"
   />
   <text class="way">自提</text>
   <view class="desc">下单享优惠</view>
   </view>
   <view class="delivery" @tap="buyWay('delivery')">
    <image
    src="@/static/delivery.png"
    mode="scaleToFill"
   />
   <text class="way">外送</text>
   <view class="desc">甜蜜送到家</view>
   </view>
 </view>
<!-- 猜你喜欢 -->
<GuessLike :GoodsList="guessLikeData"></GuessLike>
</view>

</template>
<style lang="less">
page{
  
  background-color: #F4F4F7;
}

.container{
  position: absolute;
  top: 450rpx;
  width: 95%;
  left: 0;
  right: 0;
  margin: 0 auto;
  // 会员信息
  .member-info{
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 200rpx;
  border-radius: 20rpx;
  background: linear-gradient(to right, #FFFFFF, #FFEBEF);
 margin-bottom: 20rpx;
 .avatar{
  text-align: center;

  width: 100rpx;
  height: 100rpx;
 
  image{
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }
 }
 .info{
  .phone{
   font-weight: bold;
  }
  .nickname{
   font-size: 28rpx;
  }
 }
 .other{
  display: flex;
  justify-content: space-between;
  view{
    margin: 20rpx;
  }
  .coin{
    display: flex;
    flex-direction: column;
  }
  .coupon{
    display: flex;
    flex-direction: column;
  }
  .count{
    text-align: center;
    font-weight: bold;
  }
  .field{
    color: #AAAAAA;
    font-size: 25rpx;
  }
 }
 .toLogin{
  text-align: center;
  font-size: 24rpx;
  line-height: 60rpx;
  color: #FFFFFF;
  width: 140rpx;
  background-color: #E60012;
  height: 60rpx;
  border-radius: 10rpx;
 }
}
// 购买方式
 .buy-way{
  display: flex;
   height: 280rpx;
   align-items: center;
   background-color: #FFFFFF;
   margin-bottom: 20rpx;
   border-radius: 20rpx;
   .pick-up{
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    width: 50%;
    height: 90%;
    border-right: 1rpx solid #F3F3EB;
   
   }
   .delivery{
    flex: 1;
    height: 90%;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    border-left: 1rpx solid #F3F3EB;
   }
   image{
      width: 65%;
      height: 60%;
    }
   .desc{
    font-size: 25rpx;
    letter-spacing: 20rpx;
   }
   .way{
    font-size: 38rpx;
    font-weight: bold;
    letter-spacing: 20rpx;
   }
 }

}


</style>
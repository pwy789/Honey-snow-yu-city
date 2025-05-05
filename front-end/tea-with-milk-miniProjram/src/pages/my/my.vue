<script lang="ts" setup>
const { safeAreaInsets } = uni.getSystemInfoSync();
import { useUserStore } from '@/stores/user';
import { getGuessYouLikeAPI } from '@/apis/goods';
import GuessLike from '@/components/GuessLike.vue';
import {ref} from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import type { guessLike } from '@/types/Goods';
import { getUserInfoAPI } from '@/apis/user';
const userStore = useUserStore();
//猜你喜欢数据
const guessYouLikeData = ref<guessLike[]>([]);
onLoad(async()=>{
 const res=await getGuessYouLikeAPI()
 guessYouLikeData.value=res.data
})
onShow(async()=>{
  //如果用户登录了,则每次显示这个页面的时候更新用于的数据
  if(userStore.userInfo!.token){
    const token=userStore.userInfo.token
   const res=await getUserInfoAPI()
   userStore.userInfo={...res.data,token}
  }
})
// 去登录
const checkLogin = () => {
  if (!userStore.userInfo.token) {
    uni.navigateTo({
      url: '/packageUser/pages/login/login'
    });
  }
};
//跳转到优惠券详情页
const toVoucherPage=()=>{
  uni.navigateTo({
    url:'/packageOther/pages/voucher/voucher'
  })
}
//跳转到宇王币页面
const toYuKingCoin=()=>{
  uni.navigateTo({
    url:'/packageOther/pages/coin/coin'
  })
}
//跳转到个人详情页
const toPersonDetail=()=>{
  if(!userStore.userInfo.token){
    uni.navigateTo({
    url:'/packageUser/pages/login/login'
  })
  }else{
    uni.navigateTo({
    url:'/packageOther/pages/personDetail/personDetail'
  })
  } 
 
}
</script>

<template>

  <view class="container">
    <!-- 用户信息 -->
    <view class="user-info" :style="{ paddingTop: `${safeAreaInsets!.top + 35}px` }">
      <view class="left" @tap="toPersonDetail">
        <image :src="userStore.userInfo.avatar" mode="scaleToFill" v-if="userStore.userInfo.avatar&&userStore.userInfo.avatar"/>
        <image src="@/static/user.png" mode="scaleToFill" v-else/>
        <view class="info" v-if="userStore.userInfo.token">
          <text class="phone">{{userStore.userInfo.phone}}</text>
          <text class="nickname">{{userStore.userInfo.nickname}}</text>
        </view>
        <text v-else class="info">
          您当前还未登录
        </text>
      </view>
      <!-- 登录按钮 -->
      <button class="toLogin" @tap="checkLogin" v-if="!userStore.userInfo.token">授权登录</button>
    </view>
    <!-- 宇王币与优惠券 -->
    <view class="coin-voucher">
      <!-- 宇王币 -->
      <view class="common" @tap="toYuKingCoin">
        <text>宇王币</text>
        <text class="num">{{userStore.userInfo.token?userStore.userInfo.yuKingCoin:'***'}}</text>
        <image
          src="@/static/coin.png"
          mode="scaleToFill"
        />
      </view>
      <!-- 优惠券 -->
      <view class="common" @tap="toVoucherPage">
        <text>优惠券</text>
        <text class="num">{{userStore.userInfo.token?userStore.userInfo.voucherNum:'***'}}</text>
        <image
          src="@/static/voucher.png"
          mode="scaleToFill"
        />
      </view>
    </view>
    <!-- 猜你喜欢 -->
    <GuessLike :GoodsList="guessYouLikeData"></GuessLike>
    <!-- 底部商标 -->
    <view class="footer">
      <view class="footer-top">
        <view class="footer-logo">蜜雪宇城</view>
        <view class="footer-social">
          <i class="fa-brands fa-weixin"></i>
          <i class="fa-brands fa-weibo"></i>
        </view>
      </view>
      <view class="footer-middle">
        <view class="footer-nav">
          <view class="footer-nav-item">
            <text class="nav-title">关于</text>
            <text class="nav-link">关于我们</text>
            <text class="nav-link">隐私政策</text>
          </view>
          <view class="footer-nav-item">
            <text class="nav-title">服务</text>
            <text class="nav-link">常见问题</text>
            <text class="nav-link">在线客服</text>
          </view>
        </view>
      </view>
      <view class="footer-bottom">
        <text class="copyright">© 2024 蜜雪宇城. 保留所有权利</text>
      </view>
    </view>
  </view>
</template>

<style lang="less">
page {
  background: linear-gradient(to bottom, #FFEBEF 10%, #FFFFFF 90%);
  overflow: hidden;
}

.container {
  padding: 15rpx;
  height: 100%;
}

.user-info {
  display: flex;
  justify-content: space-between;
  .left {
    display: flex;
    align-items: center;
    image {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
    }
    .info {
      margin-left: 30rpx;
      text {
        display: block;
      }
      .phone {
        color: #333333;
        font-size: 30rpx;
      }
      .nickname {
        color: #999999;
        font-size: 26rpx;
      }
    }
  }
  .toLogin {
    font-size: 28rpx;
    margin: 0;
    padding: 0;
    display: block;
    width: 180rpx;
    line-height: 60rpx;
    height: 60rpx;
    background-color: #E60012;
    color: #FBFFFF;
  }
}
//宇王币和优惠券部分
.coin-voucher {
  margin-top: 30rpx;
  display: flex;
  justify-content: space-around;
  height: 160rpx;
  margin-bottom: 30rpx;
  .common {
    line-height: 50rpx;
    background-color: #F9F9FA;
    position: relative;
    padding: 15rpx;
    width: 40%;
    color: #777777;
    border-radius: 12rpx;
  }
  .num {
    font-weight: bold;
    display: block;
    color: #333333;
  }
  image {
    right: 15rpx;
    bottom: 8rpx;
    width: 80rpx;
    height: 80rpx;
    position: absolute;
  }
}
// 底部商标区域
.footer {
  margin-top: 30rpx;
  background-color: #f8f9fa;
  border-top: 1px solid #e9ecef;
  padding: 20rpx 15rpx;
  z-index: 1;
  .footer-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15rpx;
    .footer-logo {
      font-size: 28rpx;
  font-weight: bold;
  color: #333;
  text-align: center; 
  width: 100%; 
    }
    .footer-social {
      i {
        font-size: 24rpx;
        color: #666;
        margin-left: 12rpx;
        transition: color 0.3s ease;
        &:hover {
          color: #E60012;
        }
      }
    }
  }
  .footer-middle {
    .footer-nav {
      display: flex;
      justify-content: space-around;
      .footer-nav-item {
        .nav-title {
          font-size: 22rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 8rpx;
        }
        .nav-link {
          display: block;
          font-size: 20rpx;
          color: #666;
          margin-bottom: 6rpx;
          transition: color 0.3s ease;
          &:hover {
            color: #E60012;
          }
        }
      }
    }
  }
  .footer-bottom {
    margin-top: 15rpx;
    text-align: center;
    .copyright {
      font-size: 18rpx;
      color: #999;
    }
  }
}
</style>    
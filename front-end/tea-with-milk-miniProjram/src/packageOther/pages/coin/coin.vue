<script lang="ts" setup>
import {ref} from 'vue'
import { onLoad } from '@dcloudio/uni-app';
import {getVoucherListAPI,exchangeVoucherAPI} from '@/apis/voucher'
import {getUserInfoAPI} from '@/apis/user'
import type { voucher } from '@/types/Voucher';
import { useUserStore } from '@/stores/user';
const userStore=useUserStore()
//获取屏幕的安全区域
const {safeAreaInsets} = uni.getSystemInfoSync();
//返回
const back=()=>{
  uni.navigateBack()
}
//优惠券列表
const voucherList=ref<voucher[]>([])
//获取优惠券数据
const getVoucherList=async()=>{
   const res=await getVoucherListAPI()
   voucherList.value=res.data
}

//获取用户信息
const getUserInfo=async()=>{
   const res=await getUserInfoAPI()
  const token= userStore.userInfo.token
   //更新pinia
   userStore.setUserInfo(res.data)
   userStore.userInfo.token=token
}
onLoad(()=>{
  getVoucherList()
})
//兑换
const exchange=async (voucherId:string)=>{
if(!userStore.userInfo.token){
  uni.showModal({
    title:'温馨提示',
    content:'请先登录',
    cancelText:'再逛逛',
    confirmText:'去登录',
    success:(success)=>{
      if(success.confirm){
        uni.navigateTo({
          url:'/packageUser/pages/login/login'
        })

      }
    },
  })
}
uni.showModal({
  title:'温馨提示',
  content:'确定要兑换吗？',
  cancelText:'再想想',
  confirmText:'确定',
  success:async(confirm)=>{
    if(confirm.confirm){
      await exchangeVoucherAPI(voucherId)
   await getUserInfo()
   uni.showToast({
      title:'兑换成功',
      icon:'none'
    })
    }
 
  },
})

}
</script>
<template>
  <!-- 顶部展示宇王币数量 -->
  <view :style="{paddingTop:`${safeAreaInsets!.top+10}px`}" class="top">
    <!-- 导航栏部分 -->
    <view class="navigate">
      <!-- 返回按钮 -->
      <uni-icons type="back" size="25" @tap="back"></uni-icons>
      我的宇王币
    </view>
    <!-- 宇王币数量显示 -->
     <view class="num">{{userStore.userInfo.token?userStore.userInfo.yuKingCoin:'***'}}</view>
     <image
      src="@/static/icon.png"
      mode="scaleToFill"
     />
  </view>
  <!-- 底部宇王币兑换优惠券部分 -->
   <view class="bottom" >
     <view class="title">
      <text class="title-text">精品</text>
      <text class="title-second title-text">优惠券</text>
     </view>
     <!-- 优惠券区域 -->
      <scroll-view scroll-y>
        <view class="content">
    <!-- 每一张优惠券 -->
    <view class="item" v-for="item in voucherList">
        <!-- 优惠券图片 -->
       <image  :src="item.url" mode="scaleToFill"/>
       <!-- 需要的宇王币 -->
        <text>{{item.coinNeed}}宇王币</text>
        <!-- 兑换按钮 -->
         <view class="exchange" @tap="exchange(item.id)">兑换</view>
       </view>
        </view>
    
      </scroll-view>
    </view>
</template>


<style lang="less">
page{
  position: relative;
}
.top{
  position: relative;
  height: 400rpx;
  background-color: red;
  .navigate{
  position: relative;
  text-align: center;
  color: #3C3031;
  font-weight: 520;
  uni-icons{
    position: absolute;
    top: 0;
    left: 20rpx;
  }
}
.num{
  position: absolute;
  left: 40rpx;
  bottom: 50rpx;
  font-size: 60rpx;
  color: #FFFFFF;
  font-weight: bolder;
}
image{
  position: absolute;
  right: 0;
  bottom: 25rpx;
  width: 250rpx;
  height: 250rpx;

}
}
.bottom{
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
  padding: 20rpx;
  position: absolute;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  bottom: 5rpx;
  height: 955rpx;
  background-color: #F7F7F7;
  .title{
    margin-bottom: 20rpx;
    .title-text{
    letter-spacing: 3rpx;
    font-weight: bold;
    font-size: 30rpx;
  }
  .title-second{
   color: #FD4042;
  }
  }
  scroll-view{
    .content{
      display: flex;
      flex-wrap:wrap;
      justify-content: space-between;
      height: 100%;
      .item{
       
      margin: 30rpx 0;
      position: relative;
      text{
        color: #FD5746;
        font-size: 28rpx;
        display: block;
      }
      image{
        border-radius: 15rpx;
        width: 340rpx;
        height: 210rpx;
      }
      .exchange{
        position: absolute;
        right: 0;
        bottom: 0;
        height: 45rpx;
        text-align: center;
        font-size: 26rpx;
        width: 100rpx;
        border-radius: 5rpx;
        background-color: #FD5746;
        color: #FFFFFF;
        line-height: 45rpx;
      }
    }
    }
    height: 852rpx;
    
  }
}
</style>
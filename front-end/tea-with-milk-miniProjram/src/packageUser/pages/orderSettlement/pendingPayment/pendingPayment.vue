<script lang="ts" setup>
import { useUserStore } from '@/stores/user';
import { onLoad,onUnload,onPullDownRefresh  } from '@dcloudio/uni-app';
import GoodsInfo from '../components/GoodsInfo.vue';
import { getOrderInfoAPI,cancelOrderAPI,confirmPayAPI } from '@/apis/order';
import { computed, ref } from 'vue';
import type { Orders } from '@/types/Order';
const userStore=useUserStore()
//获取屏幕安全区域
const {safeAreaInsets} = uni.getSystemInfoSync();
const props=defineProps<{
  id:string,
  from?:'order'
}>()
//存储订单数据
const ordersData=ref<Orders>({} as Orders)
//剩余支付时间
const remainPayTime=ref(0)
//预计送达时间
let EstimatedDeliveryTime:string
//获取预计送达时间
const getPendingDeliveryTime=()=>{
//预计送达时间
const now=new Date()
let hours=now.getHours().toString()
let minutes=(now.getMinutes()+7).toString() //加个七分钟
if(Number(hours)<10) hours='0'+hours
if(Number(minutes)<10) minutes='0'+minutes
return hours+':'+minutes
}
//返回
const back=()=>{
  if(props.from){
    uni.navigateBack()
  }else{
    uni.switchTab({
      url: '/pages/shop/shop'
    });

  }

}
interface OnderInfo{
  deliveryFee:number,
  packagingFee:number,
  voucherId?:string,
  voucherType?:number,
  deduct?:number,
  discountRate?:number,
  voucherName?:string,
  totalPrice:number

}
//交给GoofsInfo组件
const orderInfo=ref<OnderInfo>({} as OnderInfo)
const feeFlag=ref(false)
//定时器
let timer:any
//获取订单信息
const getOrderInfo=async()=>{
  const res=await getOrderInfoAPI(props.id)
   ordersData.value=res.data

   orderInfo.value.deliveryFee=ordersData.value.orders.deliveryFee,
   orderInfo.value.packagingFee=ordersData.value.orders.packagingFee
   orderInfo.value.totalPrice=ordersData.value.orders.totalPrice
   if(res.data.voucher){
    orderInfo.value.voucherId=res.data.voucher.id
    orderInfo.value.voucherType=res.data.voucher.type
    orderInfo.value.deduct=res.data.voucher.deduct
    orderInfo.value.discountRate=res.data.voucher.discountRate
    orderInfo.value.voucherName=res.data.voucher.name
   }
   if(ordersData.value.orders.deliveryFee){
    feeFlag.value=true
   }
   if(ordersData.value.orders.status===0){
    const createTime=new Date(ordersData.value.orders.createTime).getTime() 
  remainPayTime.value=1000*60*10-(new Date().getTime()-createTime)
 
   }

  
timer= setInterval(()=>{
  if(remainPayTime.value>0){
    remainPayTime.value-=1000
  }else{
    clearInterval(timer)
  
  }

},1000)

}
//下拉获取最新的订单状态
 onPullDownRefresh(async()=>{
 await getOrderInfo()
 uni.stopPullDownRefresh()
})
onLoad(async()=>{
 const time= getPendingDeliveryTime()
 EstimatedDeliveryTime=time
  
 await getOrderInfo()


})
//页面卸载时清除定时器
onUnload(()=>{
  if(timer){
    clearInterval(timer)
  }
})
//剩余的分钟数
const remainMintues=computed(()=>{
  return Math.max(0, Math.floor(remainPayTime.value/60000) )
})
//剩余的秒数
const remainSecond=computed(()=>{
  return Math.max(0, Math.floor(remainPayTime.value%60000/1000) )
})
//取消订单
const cancelOrder=()=>{
  uni.showModal({
    title: '温馨提示',
    content: '您确认要取消订单吗',
    success:async ({ confirm }) => {
             if(confirm){
              await cancelOrderAPI(props.id)
                uni.showToast({
                  title:'订单已取消',
                  icon:'none'
                })
                setTimeout(() => {
                  uni.switchTab({
                    url: '/pages/shop/shop'
                  });
                }, 1500);
             }
    }
  })
}
//确认支付
const pay=async()=>{
await confirmPayAPI(props.id)
uni.showToast({
  title:'支付成功',
  icon:'none'
})
//更新订单信息
getOrderInfo()
}
</script>
<!-- 带支付页面 -->
<template>
   <view class="container">
<!-- 自定义导航栏 -->
<view class="navigation" :style="{paddingTop:`${safeAreaInsets!.top+35}px`}" v-if="ordersData.orders">
  <!-- 待支付 -->
   <view class="awaitPay" v-if="remainPayTime>0&&ordersData.orders.status===0">
    <text class="status">待支付</text>
    <text class="time">剩余支付时间：{{ remainMintues<10?'0'+remainMintues:remainMintues }}:{{ remainSecond<10?'0'+remainSecond:remainSecond }}</text>
   </view>
   <!-- 订单超时未支付,已取消 -->
   <view class="awaitPay" v-if="ordersData.orders.status===1">
    <text class="status">订单已取消</text>
   </view>
   <!-- 已支付,制作中 -->
   <view class="awaitPay" v-if="ordersData.orders.status===2">
    <text class="status">订单制作中</text>
    <text>取餐号：{{ ordersData.orders.orderId }}</text>
   </view>
   <!-- 配送中 -->
   <view class="awaitPay" v-if="ordersData.orders.status===3">
    <text class="status">订单配送中</text>
    <text>取餐号：{{ ordersData.orders.orderId }}</text>
   </view>
   <!-- 已完成 -->
   <view class="awaitPay" v-if="ordersData.orders.status===4">
    <text class="status">订单已完成</text>
    <text>取餐号：{{ ordersData.orders.orderId }}</text>
   </view>
   <!-- 已送达 -->
   <view class="awaitPay" v-if="ordersData.orders.status===5">
    <text class="status">订单已送达</text>
    <text>取餐号：{{ ordersData.orders.orderId }}</text>
   </view>
   <!--  -->
   <!-- 图案 -->
    <image src="@/static/pending-payment.png" mode="scaleToFill"/>
    <!-- 返回按钮 -->
    <uni-icons type="left" size="26" color="#FFFFFF" @tap="back"></uni-icons>
</view>
<!-- 内容区域 -->
<view class="content">
 <!-- 门店信息 -->
  <view class=shop-info>
    <!-- 顶部信息 -->
    <view class="top">
      <view class="left" v-if="ordersData.orders">
        <view>{{ordersData.orders.shopName}}<uni-icons type="right" size="18"></uni-icons></view> 
        
      </view>
      <view class="right">
        <image src="@/static/phone.png" mode="scaleToFill" />
       
       <image src="@/static/wind-vane.png" mode="scaleToFill" />
      </view>
    </view>
    <!-- 底部电话等操作 -->
    <view class="bottom">
     <text class="mayTime">预计送达时间</text>
     <text class="time">约{{EstimatedDeliveryTime}}送达</text>
    </view>
  </view>
  <!-- 商品信息 -->
   <view class="goods-info">
<GoodsInfo :goods="ordersData.ogs" v-if="ordersData.ogs" :orderData="orderInfo!" :UnchoseVoucherFlag="true" :showFeeFlag="feeFlag"/>
   </view>
   <!-- 配送地址以及联系人 -->
    <view class="other" v-if="userStore.buyWay==='delivery'">
      <view class="left">
      <text class="key">配送地址</text>
      <text class="key">联系人</text>
      </view>
      <view class="right">
       <text class="value">{{ordersData.orders?.address}}</text>
       <text class="value">{{ordersData.orders?.name}} {{ordersData.orders?.phone}}</text>
      </view>
    </view>
</view>

<!-- 底部按钮 -->
 <view class="btns" v-if="ordersData.orders&&ordersData.orders.status===0">
  <button class="cancel" @tap="cancelOrder">取消订单</button>
  <button class="confirm" @tap="pay">确认支付</button>
 </view>
</view>


</template>

<style lang="scss">
.container{
  height: 100%;
  position: relative;
  .navigation{
  position: relative;
  height: 400rpx;
  background: linear-gradient(to right, #F25B68 25%, #F87540);
  .awaitPay{
    margin-top: 50rpx;
    padding: 30rpx;
    width: 400rpx;
    .status{
      font-size: 36rpx;
    }
    .time{
      font-size: 28rpx;
    }
    text{
      margin: 20rpx 0;
      font-weight: bold;
      color: #FFFFFF;
      display: block;
    }
  }
  image{
    right: 0;
    bottom: 0;
    position: absolute;
    width: 248rpx;
    height: 213rpx;
  }
  uni-icons{
    position: absolute;
    left:20rpx;
    top: 50rpx;
  }
}
.content{
 
 overflow: scroll;
  background-color: #F5F5F5;
  padding: 30rpx 30rpx 0;
  left: 50%;
  transform: translateX(-50%);
  position: absolute;
  top: 360rpx;
  width: 90%;
  height: 800rpx;
border-radius: 20rpx;
.shop-info{
  .top{
  display: flex;
  justify-content: space-between;
  .left{
    position: relative;
    width: 80%;
    view{
      margin: 10rpx 0;
    }
    uni-icons{
      position: absolute;
      right: 40rpx;
      top: 10rpx;
    }
    .distance{
      color: #83817D;
      font-size: 30rpx;
    }
  }
  .right{
    flex: 1;
    display: flex;
    justify-content: space-around;
    align-items: center;
   image{
    width: 40rpx;
    height: 40rpx;
   }
  }
 }
 .bottom{
  display: flex;
  justify-content: space-between;
  .mayTime{
    font-size: 28rpx;
    color: #8D7777;
  }
  .time{
    font-size: 28rpx;
    color: #FC3F41;
  }
 }
}

.other{
  margin-bottom: 40rpx;
  display: flex;
  padding: 20rpx;
  height: 130rpx;
 background-color: #FFFFFF;
 border-radius: 20rpx;

 .left{
  text{
    margin: 10rpx 0;
    display: block;
  }
  width: 25%;
 }
 .right{
  text{
    margin: 10rpx 0;
    display: block;
  }
  flex: 1;
 }
 .key{
  margin-right: 20rpx;
  color: #808080;
  font-size: 28rpx;
 }
 .value{
    color: #414141;
    font-size: 28rpx;
 }
}
}

.btns{
  display: flex;
  justify-content: space-between;
  width: 100%;
  position: fixed;
  bottom: 0;
  height: 120rpx;
  background-color: #FFFFFF;
  button{
    height: 100rpx;
  }
  .cancel{
    width: 35%;
    background-color: #FFFFFF;
    color: #333333;
  }
  .confirm{
    background-color: #E60012;
    color: #FDF3F3;
    width: 50%;
  }
}
}



</style>
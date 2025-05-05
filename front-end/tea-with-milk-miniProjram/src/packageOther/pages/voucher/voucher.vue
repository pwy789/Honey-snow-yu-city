<script lang="ts" setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getUserVoucherListAPI } from '@/apis/voucher';
import type { voucherData } from '@/types/Voucher';
import { useVoucherStore } from '@/stores/voucher';
const voucherStore=useVoucherStore()
const props=defineProps<{
  //选择优惠券标识,在订单模块用户如果使用优惠券则显示使用按钮
  choseVoucherFlag?:string,
  price?:number
}>()
//优惠券类型
const voucherType=[
  {name:'直减型',type:0},
  {name:'满减型',type:1},
  {name:'折扣型',type:2}
]
//选择的优惠券类型索引
const voucherTypeIndex=ref(0)
//滑动swiper时触发
const typeChange=(e:any)=>{
  voucherTypeIndex.value=e.detail.current
}
const voucherList=ref<voucherData[]>([])
onLoad(async()=>{
const res=await getUserVoucherListAPI()
voucherList.value=res.data 
voucherList.value.forEach(item=>{
  const acquireTime=new Date(item.acquireTime)
  if(item.validityPeriod){
    const temp= new Date(acquireTime.getTime()+item.validityPeriod*24*60*60*1000).toISOString()
    item.endTime=temp.substring(0,temp.indexOf('T'))
  }else{
    item.endTime=''

  }

})
if(props.price){
  //提取出非满减型优惠券
 const unThresholdVoucher= voucherList.value.filter(v=>!v.voucher.threshold)
 //提取出满足门槛的满减型优惠券
 const thresholdVoucher=voucherList.value.filter(v=>v.voucher.threshold&&v.voucher.threshold<props.price!)
 voucherList.value=[...unThresholdVoucher,...thresholdVoucher]
}
//存入pinia,只存入未使用的优惠券即可
voucherStore.setVoucherList(voucherList.value.filter(v=>v.status===0))

})
//选中了优惠券
const choseVoucher=(item:voucherData)=>{
voucherStore.setVoucherActive(item)
//返回
uni.navigateBack()
}
</script>
<template>
  <!-- 优惠券类型选择 -->
 <view class="type">
  <!-- 文字区域 -->
   <view class="text">
    <view v-for="(t,index) in voucherType" :key="t.type" @tap="voucherTypeIndex=index">{{t.name}}</view>
   </view>
   <!-- 滑轮区域 -->
   <view class="wheel" :style="{transform:`translateX(${voucherTypeIndex*100}%)`}">
    <!-- 滑轮 -->
    <view></view>
   </view>
 </view>
 <!-- swiper -->
    <swiper :current="voucherTypeIndex" @change="typeChange">
      <!-- 直减型 -->
    <swiper-item v-for="t in voucherType" :key="t.type">
      <scroll-view scroll-y v-if="voucherList.filter(v=>v.voucher.type===t.type).length>0">
        <view class="content">
          <!-- 每一张优惠券 -->
           <view class="voucher" v-for="item in voucherList.filter(v=>v.voucher.type===t.type)" :key="item.id">
            <!-- 左侧优惠券信息 -->
            <view class="left">
             <text class="name">{{item.voucher.name}}</text>
             <text>发券时间：{{item.voucher.effectiveTime.replace('T',' ')}}</text>
             <text>券有效期：{{item.acquireTime.substring(0,item.acquireTime.indexOf('T'))}} - {{item.endTime}}</text>
             <text>使用渠道：小程序</text>
             <text>适用场景：自提、外送</text>
            </view>
            <!-- 右侧优惠额度大字 -->
            <view class="right">
              <!-- 折扣型 -->
                <view class="type1" v-if="item.voucher.type===2">
                <text class="num">{{item.voucher.discountRate}}</text>
                <text class="other symbol">折</text>
              </view>   
              <!-- 满减型 -->
               <view class="type2" v-if="item.voucher.type===1">
                  <text class="symbol">￥</text>
                  <text class="num">{{item.voucher.deduct}}</text>
                  <view class="other">满{{item.voucher.threshold}}元可用</view>
              </view>   
              <!-- 直减型 -->
               <view v-if="item.voucher.type===0">
                  <text class="symbol">￥</text>
                  <text class="num">{{item.voucher.deduct}}</text>
              </view> 
            </view>
            <!-- 状态印章 -->
             <!-- 未使用 -->
             <image src="@/static/Unused.png" mode="scaleToFill" v-if="item.status===0" class="unused"/>
             <!-- 已使用 -->
             <image src="@/static/Used.png" mode="scaleToFill" v-if="item.status===1" class="used"/>
             <!-- 已过期 -->
             <image src="@/static/Expired.png" mode="scaleToFill" v-if="item.status===2"/>
             <!-- 在订单页面,如果传入了参数，会显示这个按钮，用于购买时选择优惠券的使用 -->
              <button v-if="item.status===0&&props.choseVoucherFlag" @tap="choseVoucher(item)">使用</button>
           </view>
        </view>
      </scroll-view>
      <!-- 没有优惠券时显示 -->
       <view v-else class="none">
          暂无优惠券
       </view>
    </swiper-item>

  </swiper>
 

</template>


<style lang="less">
page{
  background-color: #F5F5F5;
}
.type{
  height: 80rpx;
 
  .text{
    display: flex;
    height:70rpx;
    view{
      text-align: center;
      line-height: 70rpx;
      width: 33.3%;
      color: #333333;
    }
  }
  .wheel{
    transition: all 0.3s;
    position: relative;
    width: 33.3%;
    height: 10rpx;
    view{
       position: absolute;
       left: 50%;
       transform: translateX(-50%);
       width: 25%;
       height: 100%;
       background-color: #E60012;
    }
  }
}

  swiper{
 height: 1126rpx;
  swiper-item{
   scroll-view{
    height: 100%;
   }
    .content{
      padding: 15rpx;
      .voucher{
        position: relative;
        padding: 20rpx;
        display: flex;
        margin: 20rpx;
       background-color: #FFFFFF;
       border-radius: 20rpx;
       .left{
        text{
          margin: 10rpx;
          font-size: 26rpx;
          color: #ADADAD;
          display: block;
        }
        .name{
          color: #333333;
          font-size: 34rpx;
          font-weight: bold;

        }
       }
       .right{
        flex: 1; 
        display: flex;
        align-items: center;
        justify-content: center;
        .type1{
        display: flex;
        align-items: center;
        width: 100%;
        height: 100%;
        justify-content: center;
        
       }
       .type2{
        text-align: center;
       }
       .num{
        margin-bottom: 24rpx;
        font-size: 65rpx;
        color: #FF5759;
        font-weight: bold;
       }
       .symbol{
        color: #FF5759;
       }
       .other{
        font-size: 30rpx;
       }
       }
       button{
        position: absolute;
        right: 30rpx;
        top: 20rpx;
        width: 120rpx;
        height: 50rpx;
        color: #FFFFFF;
        background-color: #E60012;
        font-size: 28rpx;
        line-height: 50rpx;
       }
       image{
        position: absolute;
        bottom: 15rpx;
        right: 27%;
        width: 90rpx;
        height: 90rpx;
       }
       .unused{
        bottom: 0;
        right: 26%;
        width: 110rpx;
        height: 110rpx;
       }
       .used{
        width: 85rpx;
        height: 85rpx;
        bottom: 18rpx;
       }
      }
  
    }  
  
  }
}
.none{
  color: #ADADAD;
  text-align: center;
  margin: 600rpx auto;
  letter-spacing: 5rpx;
}

</style>
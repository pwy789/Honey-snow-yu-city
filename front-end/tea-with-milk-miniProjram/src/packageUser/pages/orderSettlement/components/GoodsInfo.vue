<script lang="ts" setup>
import type { buyNow, UserCart } from '@/types/user';
import { computed, onMounted, ref, watch } from 'vue';
import { useVoucherStore } from '@/stores/voucher';
import { onShow } from '@dcloudio/uni-app';

const voucherStore=useVoucherStore()

const props=defineProps<{
  goods:buyNow[],
  orderData:{
    deliveryFee:number,
    packagingFee:number,
    voucherId?:string,
    voucherType?:number,
    deduct?:number,
    discountRate?:number,
    voucherName?:string,
    totalPrice?:number
  }
  UnchoseVoucherFlag?:boolean,
  showFeeFlag:boolean
}>()
const emit=defineEmits(['totalPriceChange'])
//当购买方式为外送时生成随机配送费
const deliveryFee=ref(0)
//当购买方式为外送时生成随机打包费
const packagingFee=ref(0)
const goods=ref<UserCart[]>([])
  const originalPrice=ref(0)
onMounted(async()=>{
  goods.value=props.goods
  originalPrice.value=goods.value.reduce((pre,cur)=>pre+cur.count*cur.price,0)

 if(props.showFeeFlag){
  deliveryFee.value=props.orderData!.deliveryFee!
  packagingFee.value=props.orderData!.packagingFee!
 }
 if(!props.orderData.voucherId) return
     //选中的是直减型优惠券或者满减型
  if(props.orderData.voucherType===0||props.orderData.voucherType===1) return  discountWay.value='-'+props.orderData.deduct //扣减的价格
   //选中的是折扣型
   discountWay.value='*'+props.orderData.discountRate

})


//优惠方式
const discountWay=ref('');

onShow(()=>{

  
  if(!props.orderData.voucherId) return
     //选中的是直减型优惠券或者满减型
  if(props.orderData.voucherType===0||props.orderData.voucherType===1) return  discountWay.value='-'+props.orderData.deduct //扣减的价格
   //选中的是折扣型
   discountWay.value='*'+props.orderData.discountRate
})
//这个watch用于监视小费的变化，因为用户切换店铺或者是收货地址时，相应的小费是不一样的
watch(props.orderData,(newVal)=>{
deliveryFee.value=newVal.deliveryFee
packagingFee.value=newVal.packagingFee

})

const totalPrice=computed(()=>{
  let basePrice = goods.value.reduce((pre, cur) => pre + cur.count * cur.price, 0) 
  if (discountWay.value.startsWith('-')) {
    const deduct = Number(discountWay.value.slice(1));
    basePrice -= deduct;
  } else if (discountWay.value.startsWith('*')) {
    const discountRate = Number(discountWay.value.slice(1));
    basePrice =(basePrice* (discountRate*10))/10;
   
    
  }
  
  const num=basePrice.toString()
  //获得原价格的小数位数
   const numBit=num.indexOf('.')===-1?0:num.length-num.indexOf('.')-1;
   //获得小费的小数位数
   //1.配送费的小数位数
   const deliveryFeeBit=deliveryFee.value.toString().indexOf('.')===-1?0:deliveryFee.value.toString().length-deliveryFee.value.toString().indexOf('.')-1;
   //2.打包费的小数位数
   const packingFeeBit=packagingFee.value.toString().indexOf('.')===-1?0:packagingFee.value.toString().length-packagingFee.value.toString().indexOf('.')-1;
   //比较出最大的小数位数
   const maxBit=Math.max(numBit,deliveryFeeBit,packingFeeBit);
   basePrice=basePrice*Math.pow(10,maxBit)+deliveryFee.value*Math.pow(10,maxBit)+packagingFee.value*Math.pow(10,maxBit);
   basePrice=basePrice/Math.pow(10,maxBit);
  emit('totalPriceChange', basePrice);
  return basePrice;
})


//点击跳转到优惠券页面
const useVoucher=()=>{
  if(props.UnchoseVoucherFlag) return
  uni.navigateTo({
    url:`/packageOther/pages/voucher/voucher?choseVoucherFlag=true&price=${originalPrice.value}`
  })
}
//取消使用优惠券
const cancelVoucher=()=>{
  voucherStore.clearVoucherActive()
  props.orderData.voucherId=''
  discountWay.value=''
}
</script> 

<template>
    <uni-card >
  <!-- 每一项商品 -->
 <view class="goods" v-for="item in goods" :key="item.goodsId">
  <!-- 左边图片,名称,sku,数量 -->
  <view class="left">
    <image
      :src="item.coverImage"
      mode="scaleToFill"
    />
    <view class="info">
      <text>{{item.goodsName}}</text>
      <text>{{item.skuInfo}}</text>
      <text>x {{item.count}}</text>
    </view>
  </view>
  <!-- 右边价格 -->
  <view class="right">
     ￥{{item.count*item.price}}
  </view>
 </view>
 <!-- 小费 -->
  <view class="tip" v-if="props.showFeeFlag">
    <view>
      <text>配送费</text>
      <text>￥{{deliveryFee}}</text>
    </view>
    <view>
      <text>打包费</text>
      <text>￥{{packagingFee}}</text>
    </view>
  </view>
 <!-- 优惠券 -->
 <view class="voucher">
  <text class="title">优惠券</text>
  <text @tap="useVoucher" v-if="!props.orderData.voucherId" class="detail">{{props.UnchoseVoucherFlag?'未使用优惠券':'查看详情'}}</text>
  <view v-else> 
    <text @tap="useVoucher" class="detail">{{ props.orderData.voucherName }} {{ discountWay }}</text>
    <uni-icons type="clear" size="17" @tap="cancelVoucher" v-if="!props.UnchoseVoucherFlag"></uni-icons>


  </view>
 </view>
 <!-- 价格与奖励说明 -->
  <view class="desc">
   <view class="price">
    共{{goods?.reduce((pre,cur)=>pre+cur.count,0)}}件, 合计<text>￥{{ props.orderData.totalPrice?props.orderData.totalPrice:totalPrice }}</text>
   </view>
   <view class="reward">
    本单预计可得 (以实际到账为准) <text>{{ goods!.reduce((pre,cur)=>pre+cur.count*cur.price,0)*10 }}宇王币</text>
   </view>
  </view>
</uni-card>
</template> 

<style lang="less">
  .goods{
  height: 180rpx;
  margin: 20rpx 0;
  border-bottom: 1px #EBEEF5 solid;
 
  display: flex;
  justify-content: space-between;
  .left{
    display: flex;
    image{
     
      width: 150rpx; 
      height: 150rpx;
      border-radius: 50%;
    }
    .info{
      margin-left: 30rpx;
      text{
        display: block;
      }
      text:nth-child(1){
        font-size: 32rpx;
        color: #333333;
      }
      text:nth-child(2){
        font-size: 26rpx;
        color: #999999;
      }
      text:nth-child(3){
        font-size: 26rpx;
        color: #333333;
      }
    }
  }
  .right{
    font-size: 30rpx;
    color: #333333;
  }
 
}
// 小费
.tip{
 view{
  display: flex;
  justify-content: space-between;
  text:last-child{
    color: #333333;
}
 }
}
// 优惠券
.voucher{
  padding: 15rpx 0;
 
  margin-top: 20rpx;
  display: flex;
  justify-content: space-between;
  .title{
    font-size: 26rpx;
    color: #777777;
  }
  .detail{
    font-size: 26rpx;
    color: #E60012;
  }
  uni-icons{
    margin-left: 20rpx;
  }
}
.desc{
  border-top: 1rpx solid #EBEEF5;
  padding: 15rpx 0;
  .price{
    text-align: right;
    text{
      font-weight: bold;
      color: #333333;
    }
  }
  .reward{
    font-size: 25rpx;
    color: #AAAAAA;
    text-align: right;
    text{
      color: #FF9F00;
    }
  }
}
</style>
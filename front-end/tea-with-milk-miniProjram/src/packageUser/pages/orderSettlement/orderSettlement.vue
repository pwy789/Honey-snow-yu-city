
<script lang="ts" setup>
import {  onShow, onLoad } from '@dcloudio/uni-app';
import { useCartStore } from '@/stores/cart';
import { computed, ref, watch } from 'vue';
import type { UserCart } from '@/types/user';
import { useUserStore } from '@/stores/user';
import { useAddressStore } from '@/stores/address';
import type { Address } from '@/types/Address';
import GoodsInfo from './components/GoodsInfo.vue';
import {createOrderAPI, getOrderFeeAPI} from '@/apis/order'
import { useShopStore } from '@/stores/shop';
import { useVoucherStore } from "@/stores/voucher"
import type { ShopType } from '@/types/Shop';
const shopStore= useShopStore()
const userStore=useUserStore()
const cartStore=useCartStore()
const addressStore=useAddressStore()
const voucherStore=useVoucherStore()
//获取屏幕安全区域
const {safeAreaInsets} = uni.getSystemInfoSync();
const props=defineProps<{
  type:'buyNow'|'cart'
}>()
//用于存储商品数据
const Goods=ref<UserCart[]>([])

//存储地址数据
const addressData=ref<Address>({
  id:'',
  name:'',
  phone:'',
  address:'',
  detail:'',
  gender:0,
})
//存储店铺数据
const shopData=ref<ShopType>({} as ShopType)
//总价
const totalPrice=ref()

onLoad(async()=>{
  //如果是自提的话直接停止监听
  if(userStore.buyWay==='pick-up'){
    stop()
  }
  if(props.type==='buyNow'){
    //说明是立即购买
    Goods.value= [cartStore.buyNowGoods!]
  }else{
    //说明是购物车的方式
    Goods.value=cartStore.cartList
  }
  if(addressStore.addressActive){
   addressData.value= addressStore.addressActive
  }
  if(shopStore.shopActive){
    shopData.value= shopStore.shopActive
  }
  voucherStore.clearVoucherActive()
})
interface OrderInfo{
  deliveryFee:number
  ,packagingFee:number,
  voucherId?:string,
  voucherType?:number,
  deduct?:number,
  discountRate?:number,
  voucherName?:string}

const orderInfo=ref<OrderInfo>({
  deliveryFee:0,
  packagingFee:0
} )

const onTotalPriceChange=(price:number)=>{
totalPrice.value=price


}
onShow(()=>{
  if(addressStore.addressActive.id){
   addressData.value= addressStore.addressActive
  }
  if(shopStore.shopActive.id){
    shopData.value= shopStore.shopActive
  }
  if(voucherStore.voucherActive.id){
   const voucher= voucherStore.voucherList.find(v=>v.id===voucherStore.voucherActive.id)?.voucher
   orderInfo.value.voucherId=voucher!.id
   orderInfo.value.voucherType=voucher?.type
   orderInfo.value.deduct=voucher?.deduct
   orderInfo.value.discountRate=voucher?.discountRate
   orderInfo.value.voucherName=voucher?.name
    
  }
  
})
//返回
const back=()=>{
  uni.navigateBack()
}
//选择收货地址
const selectAddress=()=>{
  uni.navigateTo({
    url:'/packageUser/pages/address/address'
  })
}
//选择店铺
const selectShop=()=>{
  uni.navigateTo({
    url:'/packageUser/pages/pickup/pickup'
  })
}
//备注输入的内容
const notesContent=ref('')
//备注内容的长度
const notesContentLength=computed(()=>{
  return notesContent.value.length
})
//备注输入内容时触发
const onUserInput=(e:any)=>{
  //禁止输入为空
  const inputValue = e.detail.value.trim();
  // 如果输入为空，阻止默认行为并返回
  if (!inputValue) {
    e.preventDefault();
    return;
  }
  notesContent.value=inputValue
}
//确认支付
const comfirmPay=async()=>{
  if(!addressStore.addressActive?.id&&userStore.buyWay==='delivery'){
    return uni.showToast({
      title:'请选择收货地址',
      icon:'none'
    })
  }
  if(!shopStore.shopActive?.id){
    return uni.showToast({
      title:'请选择门店',
      icon:'none'
    })
  }
  let res
    //生成订单
  if(userStore.buyWay==='pick-up'){
    //自提
    res=await createOrderAPI({
  goods:Goods.value,
  totalPrice:totalPrice.value,
  gender:addressStore.addressActive.gender,
  remark:notesContent.value,
  shopId:shopStore.shopActive!.id,
  shopName:shopStore.shopActive!.name,
  voucherId:voucherStore.voucherActive.id?voucherStore.voucherActive.id:''
 })
  }else{
    //外送
    res=await createOrderAPI({
      goods:Goods.value,
      totalPrice:totalPrice.value,
  remark:notesContent.value,
  address:addressData.value.address+addressData.value.detail,
  name:addressData.value.name,
  gender:addressStore.addressActive.gender,
  phone:addressData.value.phone,
  shopId:shopStore.shopActive!.id,
  shopName:shopStore.shopActive!.name,
  deliveryFee:orderInfo.value.deliveryFee,
  packagingFee:orderInfo.value.packagingFee,
   voucherId:voucherStore.voucherActive.id?voucherStore.voucherActive.id:''
 })
    
  }
  
  if(res.code===404){
   return setTimeout(() => {
      uni.switchTab({
      url:'/pages/shop/shop'
    })
    }, 2000);
   
    
  }
  //这个id是用于区分不同订单的唯一主键id,不是用户取单时看到的订单id
  const id= res.data

  uni.navigateTo({
    url:`/packageUser/pages/orderSettlement/pendingPayment/pendingPayment?id=${id}`,
  })
}
//收货地址或者门店选择发生变化时需要调用后端接口生成新的配送费和打包费
const stop= watch([addressData,shopData],async([newVal1,newVal2])=>{
   //从后端获取配送费与打包费
   if(newVal1.id&&newVal2.id){
    //两着都有值才调用
    let count=0
    Goods.value.forEach(v=>{
        count+=v.count
    })
   const res= await getOrderFeeAPI(newVal2.id!,newVal1.id,count)
   orderInfo.value!.deliveryFee=res.data.deliveryFee
   orderInfo.value!.packagingFee=res.data.packagingFee
   }
})

</script>
<template>
  <!-- 自定义导航栏 -->
   <view class="navigate" :style="{paddingTop:`${safeAreaInsets!.top+5}px`}">
    <!-- 返回按钮 -->
    <view class="back" @tap="back">
    <uni-icons type="left" size="20" color="#FFFFFF"></uni-icons>
   </view>
      <text>订单结算</text>
   </view>
   <!-- 内容部分 -->
  <view class="container">
    <!-- 购买方式,地址区域 -->
    <uni-card :title="userStore.buyWay==='pick-up'?'自提':'外送'" class="card">
      <!-- 自提 -->
      <view class="pick-up" v-if="userStore.buyWay==='pick-up'" @tap="selectShop">
        <text >{{shopData.id?shopData!.name:'请选择门店'}} </text><uni-icons type="right" size="15" color="#403354"></uni-icons>
      </view>
      <!-- 外送 -->
      <view class="delivery" v-else>
        <text @tap="selectAddress">{{addressData.id?`${addressData.address}&nbsp;${addressData.detail}`:'请选择收货地址'}}</text>
        <uni-icons type="right" size="15" color="#403354" @tap="selectAddress"></uni-icons>
        <text>{{addressData.id?`${addressData.name}&nbsp;${addressData.phone}`:'' }}</text>
        <text class="shop" @tap="selectShop">
          {{shopData.id?shopData!.name:'请选择门店'}}
        </text> 
        <uni-icons type="right" size="15" color="#403354" @tap="selectShop"></uni-icons>
        <text class="desc">订单由三方骑手配送</text>
      </view>
</uni-card>
<!-- 商品区域 -->
<GoodsInfo :goods="Goods" 
 @totalPriceChange="onTotalPriceChange" 
 v-if="Goods.length>0" 
 :show-fee-flag="userStore.buyWay==='delivery'"
 :order-data="orderInfo"
 />
<!-- 备注 -->
<uni-card title="备注">
<textarea maxlength="30" auto-height @input="onUserInput"></textarea>
<view>{{ notesContentLength }}/30</view>
</uni-card>
  </view>
 <!-- 底部支付栏 -->
  <view class="pay">
   <text>实付: <text>￥</text> <text>{{totalPrice}}</text></text>
   <button @tap="comfirmPay">确认支付</button>
  </view>
</template>


<style lang="less" scoped>
page{
  background-color: #F5F5F5;
}
.navigate{
  position: relative;
  text-align: center;
  color: #FFFFFF;
  font-size: 28rpx;
  height: 320rpx;
  background: linear-gradient(to bottom, #FC3F41 85%, #F5F5F5 95%);
  .back{
    position: absolute;
    left: 30rpx;
    top: 15%;
  }
}
.container{
  height: 1100rpx;
  overflow: scroll;
  width: 100%;
  padding: 5rpx;
  position: absolute;
  top: 120rpx;
  .delivery{
    .shop{
      font-size: 26rpx;
      margin-right: 20rpx;
    }
    .desc{
      font-size: 26rpx;
      color: #FC3F6B;
    }
  }
  .card{
  text:nth-child(1){
    font-size: 32rpx;
    color: #333333;
  }
  text:nth-child(3){
    display: block;
    font-size: 26rpx;
    color: #999999;
  }
}
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

.voucher{
  padding: 15rpx 0;
 
  margin-top: 20rpx;
  display: flex;
  justify-content: space-between;
  text:nth-child(1){
    font-size: 26rpx;
    color: #777777;
  }
  text:nth-child(2){
    font-size: 26rpx;
    color: #E60012;
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
}
.pay{
  padding: 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 150rpx;
  position: fixed;
  bottom: 0;
  background-color: #FEFEFE;
  button{
    width: 250rpx;
    margin: 0;
    background-color: #E60012;
    color: #FCFBFF;
  }
  text{
    color: #333333;
    font-size: 26rpx;
    text:nth-child(1){
      font-weight: bold;
      font-size: 28rpx;
    }
    text:nth-child(2){
      font-weight: bold;
      font-size: 38rpx;
    }
  }
}

</style>
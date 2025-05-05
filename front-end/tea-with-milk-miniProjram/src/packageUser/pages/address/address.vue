<template>
  <view class=container>
<!-- 门店信息 -->
<view class=shopInfo>
  {{ shopStore.shopActive.id?`${shopStore.shopActive.name}：联系电话：${shopStore.shopActive.phone}`:'地址选择' }}
 </view>
 <!-- 地址区域 -->
  <scroll-view scroll-y class="address">
    <!-- 可以向右滑动进行删除操作的滑动块 -->
    <uni-swipe-action>
      <uni-swipe-action-item  :right-options="options" v-for="item in addressList" :key="item.id" @click="(e:any)=>delById(e,item.id!)">
        <view class="item" @tap="addressActive(item.id!)">
          <view class="top">{{item.address}}，{{item.detail  }}</view>
          <view class="bottom">{{item.name}}&nbsp;{{item.phone  }}</view>
          <uni-icons type="compose" size="30" @tap.stop="edit(item.id!)"></uni-icons>
        </view>
	
	</uni-swipe-action-item>
    </uni-swipe-action>
  </scroll-view>
  <!-- 底部添加地址按钮 -->
   <button class="add" @tap="addAddress">添加地址</button>
  </view>
  
</template>

<script lang="ts" setup>
import {getAddressListAPI,deleteAddressByIdAPI} from '@/apis/address'
import { ref } from 'vue'
import {  onShow } from '@dcloudio/uni-app';
import type { Address } from '@/types/Address';
import { useAddressStore } from '@/stores/address';
import { useShopStore } from '@/stores/shop';
const addressStore=useAddressStore()
const shopStore=useShopStore()
//地址列表
const addressList=ref<Address[]>([])
//获取地址列表

const  getAddressList=async()=>{
  try{
   const res= await getAddressListAPI()
   addressList.value=res.data
   //存入pinia
   addressStore.setAddressData(res.data)
  }catch(err){
    console.log('请先登录');
    return 
  }
}
onShow(async()=>{
await getAddressList()

})
//地址块右滑样式
const options=[
  {
    text:'删除',
    style:{
      backgroundColor:'#E60012',
      color:'#FFFFFF'
    }
  }
]
//添加地址
const addAddress=()=>{
  uni.navigateTo({
    url:'/packageUser/pages/address/addressOperate/addressOperate',
  })
}
//编辑
const edit=(addressId:string)=>{
  uni.navigateTo({
    url:`/packageUser/pages/address/addressOperate/addressOperate?addressId=${addressId}`,
  })
  
}
//根据id删除地址
const delById=async(e:any,id:string)=>{
 if(e.position==='right'){
  if(addressStore.addressActive?.id===id){
    addressStore.setAddressActive({} as Address)
  }
 await deleteAddressByIdAPI(id)
 getAddressList()
 }
  
}
//单击地址时即选中该地址
const addressActive=(id:string)=>{
 const addressActive= addressList.value.find(v=>v.id===id)!
 addressStore.setAddressActive(addressActive)
 //返回
uni.navigateBack()
}
</script>

<style lang="less">
.container{
  height: 100%;

  .shopInfo{
    text-align: center;
    padding: 20rpx;
  font-size: 25rpx;
  color: #FF5254;
  height: 70rpx;
  background-color: #FFF8F8;
}
.address{
  height: 960rpx;
  .item{
    position: relative;
    padding: 20rpx;
    max-height: 150rpx;
   border-bottom: 1rpx #F2F2F2 solid;
    .top{
    font-size: 29rpx;
    margin: 15rpx 0;
  }
  .bottom{
    font-size: 26rpx;
    color: #777777;
    margin: 10rpx 0;
  }
  uni-icons{
    position: absolute;
    right: 20rpx;
    top: 30rpx;
  }
  }
    
}
.add{
  margin-bottom: 40rpx;
  position: fixed;
  width: 680rpx;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  letter-spacing: 6rpx;
  color: #FDFFFF;
  border-radius: 20rpx;
  background-color: #E60012;
  height: 100rpx;
}
}

</style>
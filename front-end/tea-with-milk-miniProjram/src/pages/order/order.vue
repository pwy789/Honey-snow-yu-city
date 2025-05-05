<script lang="ts" setup>
import { ref } from 'vue';
import {getOrderListByStatusAPI,oneMoreOrderAPI}  from '@/apis/order';
import {  onShow } from '@dcloudio/uni-app';
import type { Orders } from '@/types/Order';


//获取屏幕的安全区域
const {safeAreaInsets} = uni.getSystemInfoSync();
//选中的栏索引
const orderStatusListIndex=ref(0)
//选项栏
const itemList=[
 {
  name:'未完成',
  status:[0,1]
 },
 {
  name:'进行中',
  status:[2,3]
 },
 {
  name:'已完成',
  status:[4,5]
 }
]
//订单状态常量
const  orderStatus=[
  {
    status:0,
    name:'待支付'
  },
  {
    status:1,
    name:'已取消'
  },
  {
    status:2,
    name:'制作中'
  },
  {
    status:3,
    name:'配送中'
  },
  {
    status:4,
    name:'已完成'
  },
  {
    status:5,
    name:'已送达'
  }
  
]
const loadingStatus=ref()

interface pageCondition{
  page:number,
  pageSize:number,
  //count用于接收后端传来的数据的总条数,防止没有数据了而还去发请求
  count:number
}
//分页查询参数
const queryPages:pageCondition[]=[
  {
    page:1,
    pageSize:5,
    count:0
  },
  {
    page:1,
    pageSize:5,
    count:0
  },
  {
    page:1,
    pageSize:5,
    count:0
  }
]
interface OrderListByStatus {
  [key: string]: Orders[];
  NotCompleted: Orders[];
  inProgress: Orders[];
  completed: Orders[];
}
//不同状态下的结果集
const orderListByStatus=ref<OrderListByStatus>({
  NotCompleted:[], 
  inProgress:[], 
  completed:[]
}
)
const keys=Object.keys(orderListByStatus.value)
//控制页面下拉刷新的动画
const pullDownAnimation=ref(false)

//页面下拉刷新时触发
const  onRefresh=async()=>{
 //先清除当前页的数据，并将page的值置为1
 orderListByStatus.value[keys[orderStatusListIndex.value]]=[]
 queryPages[orderStatusListIndex.value].page=1
 //发起请求
await getNextPageData(queryPages[orderStatusListIndex.value],itemList[orderStatusListIndex.value].status)
pullDownAnimation.value=false
  
}


//获取下一页的数据
const getNextPageData=async({page,pageSize}:pageCondition,status:number[])=>{
  loadingStatus.value='loading'
  const res= await getOrderListByStatusAPI({
    page:page,
    pageSize,
    status
  })
  loadingStatus.value='more'
  orderListByStatus.value[keys[orderStatusListIndex.value]].push(...res.data.ovs)
  queryPages[orderStatusListIndex.value].count=res.data.count
  if(orderListByStatus.value[keys[orderStatusListIndex.value]].length>=res.data.count) loadingStatus.value='no-more'
}
//页面显示时获取未完成的订单
onShow(async()=>{
  orderListByStatus.value.NotCompleted=[]
  orderListByStatus.value.inProgress=[]
  orderListByStatus.value.completed=[]
  getNextPageData(queryPages[orderStatusListIndex.value],itemList[orderStatusListIndex.value].status)
})
const onSwiperChange=(e:any)=>{
  orderStatusListIndex.value=e.detail.current
  //如果当前一栏还没有数据,就获取
  if(!orderListByStatus.value[keys[orderStatusListIndex.value]].length){
    getNextPageData(queryPages[orderStatusListIndex.value],itemList[orderStatusListIndex.value].status)
  }
}
//滑到底部时获取下一页数据
const onScrollToLower=()=>{
  const page=queryPages[orderStatusListIndex.value].page
  const pageSize=queryPages[orderStatusListIndex.value].pageSize
  const count=queryPages[orderStatusListIndex.value].count
  //没有下一页了
  if(page*pageSize>count)   return
  
  queryPages[orderStatusListIndex.value].page++
  //获取下一页数据
  getNextPageData(queryPages[orderStatusListIndex.value],itemList[orderStatusListIndex.value].status)
}
//获取订单详情
const getOrderDetail=(id:string)=>{

  uni.navigateTo({
    url:`/packageUser/pages/orderSettlement/pendingPayment/pendingPayment?id=${id}&from=order`
  })
}
//再来一单
const oneMoreOrder=async (id:string)=>{
  //将该订单的商品加入到购物车
  await oneMoreOrderAPI(id)
  //跳转到shop页面
  uni.switchTab({
    url:'/pages/shop/shop'
  })
  
}
</script>
<template>
  <!-- 自定义头部导航栏 -->
   <view class="navigate" :style="{paddingTop:`${safeAreaInsets!.top+35}px`}">
    <view class="type">
  <!-- 文字区域 -->
   <view class="text">
    <view v-for="(item,index) in itemList" :key="item.name" @tap="orderStatusListIndex=index">{{ item.name }}</view>
   </view>
   <!-- 滑轮区域 -->
   <view class="wheel" :style="{transform:`translateX(${orderStatusListIndex*100}%)`}">
    <!-- 滑轮 -->
    <view></view>
   </view>
 </view>
   </view>

   <!-- swiper -->
    <swiper @change="onSwiperChange" :current="orderStatusListIndex">
      <swiper-item v-for="l in itemList" :key="l.name">
        <scroll-view scroll-y @scrolltolower="onScrollToLower" 
         refresher-enabled="true" :refresher-triggered="pullDownAnimation"
        :refresher-threshold="100" refresher-background="#F5F5F5" 
        @refresherrefresh="onRefresh"
        
        >
          <view class="content">
         <!-- 每一个盒子为一个订单 -->
           <view class="box" v-for="item in orderListByStatus[keys[orderStatusListIndex]]" :key="item.orders.id" @tap="getOrderDetail(item.orders.id)">
            <view class="top">
              <text class="buyway">{{ item.orders.deliveryFee?'外送':'自提' }}</text>
              <text class="shopName">{{ item.orders.shopName}}</text>
              <uni-icons type="right" size="18"></uni-icons>
              <!-- 订单状态 -->
               <text class="status">{{ orderStatus.find(v=>v.status===item.orders.status)?.name }}</text>
            </view>
            <view class="middle">
              <image :src="i.coverImage" mode="scaleToFill" v-for="i in item.ogs.slice(0,item.ogs.length<4?item.ogs.length:4)" :key="i.coverImage"/>
              <!-- 如果图片太多了,显示这三个点 -->
             <text v-if="item.ogs.length>5">...</text>
            </view>
            <view class="bottom">
              <view class="up">
                 <text class="time">{{ item.orders.createTime.replace('T',' ') }}</text>
                 <text class="time">共{{ item.ogs.reduce((cur,pre)=>cur+pre.count,0) }}件<text class="price">￥{{ item.orders.totalPrice }}</text></text>
              </view>
              <view class="down">
                <button @tap.stop="oneMoreOrder(item.orders.id)">再来一单</button>
              </view>
            </view>
           </view>
          </view>
        <!-- 加载更多 -->
 <uni-load-more iconType="circle" :status="loadingStatus" />
        </scroll-view>
      </swiper-item>
         
    </swiper>

		

</template>



<style lang="less">
page{
  background-color: #F5F5F5;
}
.navigate{
  background-color: #FFFFFF;

  height: 220rpx;
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
}
// swiper
swiper{
  height: 1018rpx;
  scroll-view{
    height: 100%;

  }
  .content{
    padding: 15rpx;
    .box{
      margin: 20rpx 5rpx;
      padding: 15rpx;
      border-radius: 20rpx;
    background-color: #FFFFFF;
   
    .top{
      position: relative;
      display: flex;
      .buyway{
        width: 45rpx;
        height: 45rpx;
        border-radius: 50%;
        color: #FF5254;
        background-color: #FFF8F8;
        line-height: 40rpx;
      font-size: 22rpx;
      font-weight: bold;
    }
    .shopName{
      margin-left: 15rpx ;
      font-size: 28rpx;
    }
    .status{
      font-size: 26rpx;
      color: #AAAAAA;
      position: absolute;
      right: 10rpx;
      top: 0;
    }
    }
    .middle{
      margin-top: 10rpx;
      display: flex;
      image{
        margin-right: 30rpx;
        width: 100rpx;
        height: 100rpx;
        border-radius: 50%;
      }
    }
    .bottom{
      margin-top: 20rpx;
      .up{
        display: flex;
        justify-content: space-between;
        .time{
          color: #ADADAD;
          font-size: 25rpx;
          .price{
            font-size: 30rpx;
            color: #333333;
            font-weight: bold;
          }
        }
      }
      .down{
        height: 80rpx;
        position: relative;
         button{
          position: absolute;
          right: 0;
          top: 15rpx;
          margin: 0;
          padding: 0;
          background-color: #FFFFFF;
          border: 2rpx #E60012 solid;
          width: 150rpx;
          height: 60rpx;
          font-size: 25rpx;
          color: #E60012;
         }
      }
    }
  }
  }

}
</style>
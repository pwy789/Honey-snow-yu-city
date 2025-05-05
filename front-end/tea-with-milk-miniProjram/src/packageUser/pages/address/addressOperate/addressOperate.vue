<script lang="ts" setup>
import { ref, watch } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { useAddressStore } from '@/stores/address';
import type { Address } from '@/types/Address';
import {editAddressAPI,addAddressAPI} from '@/apis/address'
const addressStore=useAddressStore()
const {safeAreaInsets} = uni.getSystemInfoSync();
const formRef=ref()
const props=defineProps<{
  addressId?:string
}>()
//返回
const back=()=>{
  uni.navigateBack()
}
//用户定位
const userLocation=ref<{longitude:number,latitude:number}>({
  longitude:0, //经度
  latitude:0  //纬度
})
//中心点
const markers=ref([
  {
    id:1,
    longitude:userLocation.value.longitude,
    latitude:userLocation.value.latitude,
    iconPath:'/static/position.png',
    width:40,
    height:40,
    callout:{
      content:'当前位置',
      color:'#fff',
      fontSize:12,
      borderRadius:10,
      bgColor:'#1F1F1F',
      padding:10,
      display:'ALWAYS'
    }
  }
])
watch(userLocation.value,(newVal)=>{
  markers.value[0].longitude = newVal.longitude
  markers.value[0].latitude = newVal.latitude
})
//某个经纬度下总的条数
const count=ref(0)
const countForSearch=ref(0)
//地址类型声明
interface placeNearBy{
  id:string,
  title:string,
  ad_info:{
    city:string,
    district:string,
    province:string
  }
  address:string,
  location:{
    lat:number,
    lng:number
  }

}
//用于存储用户附近的地点
const placeNearBy=ref({
  //分页参数
  page_index:1,
  list:[] as placeNearBy[],
})
//用户根据搜索得到的地址列表
const plaaceBySearch=ref({
  page_index:1,
  list:[] as placeNearBy[],
})
onLoad(()=>{
  //如果有Id,说明是编辑操作
  if(props.addressId){
   formData.value= addressStore.addressData.find(v=>v.id===props.addressId)!
  }
  //获取用户定位
  uni.getFuzzyLocation({
    type: 'wgs84',
    success:async(res)=>{
      //经度
     userLocation.value.longitude=res.longitude
     //纬度
     userLocation.value.latitude=res.latitude
     
     //查询附近的商铺
    const v=await callMapAPI( userLocation.value.latitude, userLocation.value.longitude)
    if(v.data){
      placeNearBy.value.list=v.data
      count.value=v.count
    }
 
    }
  })
})

//表单数据
const formData=ref<Address>({
  name:'',
  gender:0,
  phone:'',
  address:'',
  detail:'',
  longitude:0,
  latitude:0

})
//校验规则
const rules={
  name:{
    rules:[
    {
      required:true,
      errorMessage:'请输入联系人'
    }
    ]
  },
  phone:{
    rules:[
    {
      required:true,
      errorMessage:'请输入手机号'
    },
    {
      pattern:/^1[3-9]\d{9}$/,
      errorMessage:'请输入正确的手机号'
    }
    ]
  },
  address:{
    rules: [
    {
      required:true,
      errorMessage:'请输入地址'
    }
  ]
  }
 ,
  detail:{
    rules:
    [
    {
      required:true,
      errorMessage:'请输入详细地址'
    }
  ]
  }
 
}
//性别选项
const sexs=[
    {text:'男',value:0},
    {text:'女',value:1}
  ]
  //取消选择地址
const cancelSelectAddress=()=>{
  isShowMap.value=false
  isShowInput.value=false
}
//控制地图view的显示与隐藏
const isShowMap=ref(false)
//控制输入框的显示与隐藏
const isShowInput=ref(false)
//用户输入的地址
const addressInp=ref('')
//保存
const save=async()=>{
  try{
    await formRef.value.validate()
  }catch(err:any){
    console.log(err[0].errorMessage)
    return
  }
  //执行到这里,说明表单填写没问题
  if(props.addressId){
    //编辑操作
   await editAddressAPI({id:props.addressId,...formData.value  })
  }else{
    //新增操作
    await addAddressAPI(formData.value)
  }
  //回到地址页
  uni.navigateBack()
  
}

 //根据关键词搜索地址
const callMapAPI=(latitudea:number,longitude:number,page=1,keyword='小区')=>{
  return new Promise<{data:any,count:number}>((resolve,reject)=>{
 
  uni.request({
    url:'https://apis.map.qq.com/ws/place/v1/search',
    method:'GET',
    data:{
      key:'7BVBZ-7B46B-KB6UB-NIHEE-IRRK7-DYBQM',
      keyword:keyword,
      boundary:`nearby(${latitudea},${longitude},1000,1)`,
      page_index:page
      
    },
    success:(res:any)=>{
      const val = res.data.data as placeNearBy[];
      const cou=res.data.count
      resolve({
        data:val,
        count:cou,
      })
      
    },
  })
  })

}
//调用地图地点搜索接口
const callMapSearchAPI=(page_index=1)=>{
  return new Promise<{data:any,count:number}>((resolve,reject)=>{
    if(addressInp.value=='') return

uni.request({
    url:'https://apis.map.qq.com/ws/place/v1/suggestion',
    data:{
      key:'7BVBZ-7B46B-KB6UB-NIHEE-IRRK7-DYBQM',
      keyword:addressInp.value, 
      page_index,
      page_size:10
    },
    success:(res:any)=>{
  resolve({
    data:res.data.data,
    count:res.data.count
  })
    },

  })
  })

}
//显示input输入框
const showInput=()=>{
  isShowInput.value=true
  addressInp.value=''
}
//节流
let timerForThrottle:any
//地点选择滚动到底时获取下一个附近店铺数据
const onScrollToLower=()=>{
  if(placeNearBy.value.page_index*10>=count.value) return
  //节流
  if(!timerForThrottle){
    timerForThrottle=setTimeout(async()=>{
    placeNearBy.value.page_index++
  const res= await callMapAPI(userLocation.value.latitude,userLocation.value.longitude,placeNearBy.value.page_index)
  placeNearBy.value.list.push(...res.data)
   timerForThrottle=null
  },2000)
}
}
//搜索滚动到底部时根据关键字继续获取下一页数据
const onSearchScrollToLower=async()=>{
  if(plaaceBySearch.value.page_index*10>=countForSearch.value) return
 plaaceBySearch.value.page_index++
const res=await callMapSearchAPI(plaaceBySearch.value.page_index)
plaaceBySearch.value.list.push(...res.data)
}
//用于存储用户选择的地址id
const userSelectedPlace=ref<{id:string,title:string,longitude:number,latitude:number}>({} as {id:string,title:string,longitude:number,latitude:number})
//用户选择了一个地点
const onSelectPlace=(placeId:string)=>{
 const p= placeNearBy.value.list.find(v=>v.id===placeId)
 userLocation.value.latitude=p!.location.lat
 userLocation.value.longitude=p!.location.lng
 userSelectedPlace.value!.id=placeId
 userSelectedPlace.value!.title=p!.title
 userSelectedPlace.value!.longitude=p!.location.lng
 userSelectedPlace.value!.latitude=p!.location.lat
}
//防抖
let timerForDebounce:any
//用户输入监听
const onUserInp=()=>{
  plaaceBySearch.value.page_index=1
  clearTimeout(timerForDebounce)
  timerForDebounce=setTimeout(async()=>{
  const res= await callMapSearchAPI()
    plaaceBySearch.value.list=res.data
    countForSearch.value=res.count
  },500)
}
//用户通过搜索点击选择了一个地址
const onSelectForSearch=async(addressId:string)=>{
userSelectedPlace.value!.id=addressId
userSelectedPlace.value!.title=plaaceBySearch.value.list.find(v=>v.id===addressId)!.title
userSelectedPlace.value!.longitude=plaaceBySearch.value.list.find(v=>v.id===addressId)!.location.lng
userSelectedPlace.value!.latitude=plaaceBySearch.value.list.find(v=>v.id===addressId)!.location.lat
const address= plaaceBySearch.value.list.find(v=>v.id===addressId)
userLocation.value.latitude=address!.location.lat
userLocation.value.longitude=address!.location.lng
placeNearBy.value.page_index=1
const res=await callMapAPI(userLocation.value.latitude,userLocation.value.longitude,placeNearBy.value.page_index,address?.title)
placeNearBy.value.list=res.data
count.value=res.count
//返回到地图页面
isShowInput.value=false
}
//点击确认按钮时触发
const confirm=()=>{
  if(!userSelectedPlace.value?.id){
   return uni.showToast({
      title:'请选择地址',
      icon:'none'
    })
  }
  formData.value.address=userSelectedPlace.value.title
  formData.value.longitude=userSelectedPlace.value.longitude
  formData.value.latitude=userSelectedPlace.value.latitude
  //关闭map
  isShowMap.value=false
}
//地图视野变化时触发
const onRegionChange=async(e:any)=>{
if(e.type==='end'){
  //用户手指松开后再定位
  userLocation.value.latitude=e.detail.centerLocation.latitude
  userLocation.value.longitude=e.detail.centerLocation.longitude
  placeNearBy.value.page_index=1
  //重新定位
 const res=await callMapAPI(userLocation.value.latitude,userLocation.value.longitude,placeNearBy.value.page_index)
 placeNearBy.value.list=res.data
 count.value=res.count
}

}
</script>

<template>
  <!-- 标题部分:添加收货地址或者编辑地址 -->
<view class="title" :style="{paddingTop:`${safeAreaInsets!.top+5}px`}">
  <!-- 返回按钮 -->
  <uni-icons type="left" size="20" class="back" @tap="back"></uni-icons>
  {{props.addressId?'编辑地址':'添加收货地址'}}
</view>
<view class="container" v-if="!isShowMap">
<!-- 表单区域 -->
<view class=form >
  <uni-forms :modelValue="formData" :rules=rules ref="formRef">
			<uni-forms-item label="联系人" name="name">
				<uni-easyinput type="text" v-model="formData.name" placeholder="请输入姓名" />
			</uni-forms-item>
			<uni-forms-item label="性别" name="age">
				<uni-data-checkbox v-model="formData.gender" :localdata="sexs" />
			</uni-forms-item>
			<uni-forms-item label="手机号"  name="phone">
				<uni-easyinput v-model="formData.phone" placeholder="请输入手机号" />
			</uni-forms-item>
      <uni-forms-item label="地址"  name="address" @tap="isShowMap=true">
        <!-- 点击跳转到地图选择地址 -->
				<input type="text" disabled="true" v-model="formData.address" placeholder="请选择地址">
   
          <uni-icons type="right" size="15" class="right"></uni-icons>
        
			</uni-forms-item>
      <uni-forms-item label="详细地址"  name="detail">
        <uni-easyinput v-model="formData.detail" placeholder="请输入详细地址" />
			</uni-forms-item>
		</uni-forms>
 </view>
 <!-- 底部保存按钮 -->
 <button class="save" @tap="save">保存</button>
</view>
<!-- 地图部分 -->
 <view class="visualization" v-else>
  <!-- 取消与确认按钮 -->
 <view class="btn">
<text @tap="cancelSelectAddress">取消</text> <text @tap=confirm>确定</text>
 </view>

  <view class="content" v-if="!isShowInput">
     <!-- 搜索框 -->
     <view class="search" @tap="showInput">
      <uni-icons type="search" size="20" color="#999999"></uni-icons>
    搜索地点
     </view>
     <!-- 地图区域 -->
    <map class="map" :longitude="userLocation.longitude" :latitude="userLocation.latitude" :markers="markers" show-location enable-3D @regionchange="onRegionChange"></map>
    <!-- 地点选择区域 -->
     <scroll-view scroll-y  class="place" @scrolltolower="onScrollToLower">
      <!-- 每一个地点 -->
      <view v-for="item in placeNearBy.list" :key="item.id" @tap="onSelectPlace(item.id)" class="item">
        <!-- 详细位置 -->
        <text>{{item.title}}</text>
        <!-- 省市区 -->
        <text>{{item.address}}</text>
        <uni-icons type="checkbox" size="20" v-if="userSelectedPlace?.id===item.id" color="#61ACFF"></uni-icons>

      </view>
      <!-- 滑到底时显示 -->
      <view v-if="placeNearBy.page_index*10>=count" class="noMore">
      没有更多了...
      </view>
      <!-- 加载中 -->
      <view v-else>
        <uni-load-more iconType="auto" status="loading"  />
      </view>
     </scroll-view >
  </view>
  <!-- 显示输入框，供用户输入地址定位导航 -->
   <view class="content" v-else>
    <!-- 输入框 -->
    <view class="input">
      <uni-icons type="search" size="20" color="#999999" v-if="!addressInp"></uni-icons>
      <text v-if="!addressInp" class="placeholder">搜索地点</text>
      <input type="text" v-model="addressInp" @input="onUserInp"/> 
      <text class="cancel" @tap="isShowInput=false">取消</text>
    </view>
   <!-- 地址列表 -->
    <view class="addresslist">
      <scroll-view scroll-y  class="place" v-if="addressInp" @scrolltolower="onSearchScrollToLower">
      <!-- 每一个地点 -->
      <view v-for="item in plaaceBySearch.list" :key="item.id" @tap="onSelectForSearch(item.id)">
        <!-- 详细位置 -->
        <text>{{item.title}}</text>
        <!-- 省市区 -->
        <text>{{item.address}}</text>
      </view>
        <!-- 滑到底时显示 -->
        <view v-if="plaaceBySearch.page_index*10>=countForSearch" class="noMore">
      没有更多了...
      </view>
      <!-- 加载中 -->
      <view v-else>
        <uni-load-more iconType="auto" status="loading" />
      </view>
     </scroll-view >
    </view>
   </view>
 </view>
</template>


<style lang="less">
page{
 
 display: flex;
 flex-direction: column;
 background-color: #F5F5F5; 
}
.title{
  position: relative;
  color: #000000;
  text-align: center;
  height: 120rpx;
  background-color: #FFFFFF;
  .back{
    position: absolute;
    left: 25rpx;
  }
}
.container{
  height: 100%;
  .form{
  position: relative;
  padding: 20rpx;
  margin-top: 30rpx;
  height: 600rpx;
  background-color: #FFFFFF;
  
  .right{
    position: absolute;
    right: 20%;
    top: 10rpx;
  }
}
.save{
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
.visualization{
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
  
  background-color: #FFFFFF;
  .btn{
    margin-top: 50rpx;
    height: 40rpx;
    padding:0 30rpx;
    display: flex;
    justify-content: space-between;
  }
  .content{
    display: flex;
    flex-direction: column;
    margin-top: 20rpx;
    overflow: hidden;
    flex: 1;
    .map{
      margin-top: 20rpx;
      width: 750rpx;
      height: 550rpx;
    }
    .search{
      letter-spacing: 4rpx;
      text-align: center;
      color: #969696;
      font-size: 25rpx;
      position: relative;
      height: 50rpx;
      line-height: 50rpx;
      background-color: #EBEBEB;
      width: 90%;
      border-radius: 10rpx;
      margin: 0 auto;
      uni-icons{
        position: absolute;
        top: 7%;
        left: 39%;
        transform: translateX(-50%);
      }
    }
    .place{
    overflow: scroll;
     flex: 1;
     .noMore{
      text-align: center;
      font-size: 25rpx;
     }
     view{
      padding: 20rpx;
      height: 130rpx;
      border-bottom: 2rpx solid #D3D3D3;
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      text:nth-child(1){
        color: rgb( 51, 51, 51);
     }    
     text:nth-child(2){
      color: #808080;
      font-size: 25rpx;
      max-width: 600rpx;
     }  
     }
    .item{
      position: relative;
      uni-icons{
        position: absolute;
        right: 20rpx;
        top: 45rpx;
      }
    }
    }
    .input{
      height: 60rpx;
      position: relative;
      padding-left: 30rpx;
      padding-right: 40rpx;
      display: flex;
      justify-content: space-between;
      input{
        height: 100%;
        padding-left: 30rpx;
      background-color: #EBEBEB;
      width: 80%;
      border-radius: 15rpx;
    }
    .placeholder{
      font-size: 26rpx;
      color: #898989;
      position: absolute;
      left: 100rpx;
      top: 15rpx;
    }
    .cancel{
      margin-top: 10rpx;
      color: #0079FF;
      font-size: 35rpx;
    }
    uni-icons{
      top: 10rpx;
      position: absolute;
      left: 55rpx;
    }
    }
    .addresslist{
      flex: 1;
      overflow: scroll;
      .place{
        height: 100%;
      }
    }
  }
 
}
</style>
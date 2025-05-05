<script lang="ts" setup>
import { ref } from 'vue';
import {userLoginAPI} from '@/apis/user'
const {safeAreaInsets} = uni.getSystemInfoSync();
import { useUserStore } from '@/stores/user';
const userStore=useUserStore()
const formRef=ref()
//用户输入的手机号
const formData = ref({
  phone:''
})
//是否同意条款
const isAgree=ref(false)

//点击是否同意条款时触发
const onTapRadio=()=>{
  isAgree.value=!isAgree.value
  
}
const rules=ref({
  phone:{
    rules:[
      {
        required:true,
        errorMessage: '手机号不能为空',
      },
      
      {
        pattern:/^1[3-9]\d{9}$/,
        errorMessage: '手机号格式不正确',
      }
    ]
  }
})
//返回
const back=()=>{
  const pages=getCurrentPages()
  if(pages.length<=1){
    uni.switchTab({ url: '/pages/index/index' })
  }else{
    uni.navigateBack()
  }
}
//验证登录
const toLogin=async()=>{
  try{
    await formRef.value.validate()
  }catch(err:any){
    console.log(err[0].errorMessage)
    return
  }
  if(!isAgree.value){
    uni.showToast({
      title:'请先阅读并同意条款',
      icon:'none'
    })
    return
  }
     uni.login({
      success:async(res)=>{
   const code=res.code
  const val=await userLoginAPI(formData.value.phone,code)
  userStore.setUserInfo(val.data)
  back()
      },
     })

}
</script>
<template>
  <!-- 自定义导航栏 -->
  <view class="navigation" :style="{paddingTop:`${safeAreaInsets!.top+5}px`}"> 
      <!-- 返回按钮 -->
  <uni-icons type="left" size="20" color="#333333" class="back" @tap="back"/>
    蜜雪宇城
  </view>


   <view  class="login">
      <!-- logo -->
    <image
    src="@/static/logo.png"
    mode="scaleToFill"
   />
   <text>蜜雪宇城</text>
   <text>成为会员，立享更多优惠福利</text>
   <text>授权绑定手机号为您提供更好的服务</text>
   <!-- 模拟手机号登录 -->
   <uni-forms :modelValue="formData" :rules="rules" ref="formRef" validateTrigger="blur">
			<uni-forms-item name="phone">
				<uni-easyinput type="text" v-model="formData.phone" placeholder="请输入手机号" />
			</uni-forms-item>
		</uni-forms>
    <button @tap="toLogin">登录</button>
    <!-- 同意条款 -->
     <radio-group >
      <label class="uni-list-cell uni-list-cell-pd">
        <radio
          :checked="isAgree"
          @tap="onTapRadio"
          color="#E60012"
        />
       已阅读并同意 <text>《蜜雪宇城个人信息保护政策》《蜜雪宇城用户服务协议》</text>
      </label>
     </radio-group>
   </view>
   
</template>



<style lang="less">
// 导航栏区域
.navigation{
  position: relative;
  text-align: center;
  font-size: 28rpx;
  .back{
    position: absolute;
    left: 20rpx;
  }
}
// 内容部分
.login{
 text-align: center;
  width: 550rpx;
  height: 100%;
  margin: 140rpx auto;
   text{
    display: block;
    &:nth-child(3){
    margin-top: 80rpx;
    font-weight: bold;
   }
   &:nth-child(4){
    margin-top: 80rpx;
    margin-bottom: 20rpx;
    color: #77778D;
    font-size: 25rpx;
   }
   }
  image{
    margin: 0 auto;
    display: block;
    width: 210rpx;
    height:170rpx;
  }
  button{
    margin-bottom: 20rpx;
    background-color: #E60012;
    color: #ffffff;
  }
  radio-group{
      radio{
        transform: scale(0.7);
      }
      font-size: 25rpx;
      text{
        display: inline;
        color: #EA0012;
      }
  }
}
</style>
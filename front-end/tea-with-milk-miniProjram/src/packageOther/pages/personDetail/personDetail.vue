<template>
  <!-- 选择头像区域 -->
  <view class="avatar">
   
    <!-- 存放头像的拍照图标的容器 -->
    <view class="box">
    <button open-type="chooseAvatar" @chooseavatar="chooseAvatar" class="img" :style="{background:`url(${formData.avatarUrl?formData.avatarUrl:userURL}) no-repeat center center / 100%`}"></button>
    <button open-type="chooseAvatar" @chooseavatar="chooseAvatar" class="icon" :style="{background:`url(${iconURL}) no-repeat center center / 100%`}"></button>
    </view> 
  </view>
    <!-- 信息输入部分 -->
    <view class="infoInp">
      <uni-forms :modelValue="formData">
			<uni-forms-item label="姓名" name="name">
				<input type="nickname" placeholder="请输入昵称" v-model="formData.name">
			</uni-forms-item>
			<uni-forms-item label="手机" name="phone">
				<input type="text" disabled v-model="formData.phone" />
			</uni-forms-item>
			<uni-forms-item  label="性别" name="gender">
        <uni-data-checkbox  v-model="formData.gender" :localdata="sexs" />
			</uni-forms-item>
		</uni-forms>
    <!-- 修改手机号 -->
     <view class="changePhone" @tap="showDialog=true">修改</view>
    </view>
    <!-- 文字描述部分 -->
     <text class="desc">
      根据未成年人保护相关法律法规的要求，我们不主动处理14周岁以下未成年
      人的个人信息。如果您为14周岁以下的用户，请勿填写您的个人资料。</text>
      <!-- 保存按钮 -->
       <button class="save" @tap="save">保存</button>
       <!-- 退出登录 -->
        <text class="logout" @tap="logout">退出登录</text>
        <!-- 点击修改时显示的弹出层 -->
     <view class="dialog" :class="{'showDialog':showDialog}">
      <!-- 关闭按钮 -->
      <uni-icons type="clear" size="30" class="icon" @tap="closeDialog"/>
      <!-- 温馨提示 -->
      <view class="toast">温馨提示</view>
     <view class="desc">为保障您正常使用 <text class="software">蜜雪宇城小程序</text>，请在绑定前确定新手机号未绑定 <text class="software">蜜雪宇城</text> </view> 
      <!-- 输入部分 -->
      <view class="inp">
              <input type="text" placeholder="请输入手机号" v-model="changePhoneForm.phone">
              <input type="text" placeholder="请输入验证码" v-model="changePhoneForm.code">
               <!-- 获取验证码 -->
       <text class="getcode" @tap.stop="getcode" :class="{'disable-get-code':timerFlag}">{{timerFlag?`${countdown}s后重新获取`:'获取验证码'}}</text>
      </view>
     
      <!-- 确认按钮 -->
       <button class="confirm" @tap="confirm">确认</button>
     </view>
       <!-- 蒙层 -->
        <view class="mask" v-show="showDialog" :class="{'showMask':showDialog}"></view>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getUserInfoAPI,saveUserInfoAPI,getCodeAPI,confirmChangePhoneAPI } from '@/apis/user';
import { useAddressStore } from '@/stores/address';
import { useCartStore } from '@/stores/cart';
import { useShopStore } from '@/stores/shop';
import { useUserStore } from '@/stores/user';
import { useVoucherStore } from '@/stores/voucher';
const userStore=useUserStore()
const addressStore=useAddressStore()
const shopStore=useShopStore()
const cartStore=useCartStore()
const voucherStore=useVoucherStore()
onLoad(async ()=>{
  //获取用户的基本信息
 const res=await getUserInfoAPI()
 formData.value={
  name:res.data.nickname,
  phone:res.data.phone,
  gender:res.data.gender,
  avatarUrl:res.data.avatar
}
})
//性别
const sexs=[
  {
    text:'男',
    value: 0
  },
  {
    text:'女',
    value: 1
  }
  
]
//form表单数据
const formData=ref<{name:string,phone:string,gender:number,avatarUrl:string}>({
   name:'',
   phone:'',
   gender:0,
   avatarUrl:''
})
//修改手机号form表单
const changePhoneForm=ref<{phone:string,code:string}>({phone:'',code:''})
const showDialog=ref(false)
//手机号正则验证
const phoneRegex=/^1[3-9]\d{9}$/
//验证码正则验证
const codeRegex=/^\d{4,6}$/
//定时器
let timer:any
//倒计时
const countdown=ref(60)
const timerFlag=ref(false)
//获取验证码
const getcode=async()=>{
 
  if(timer) return
  
 if(!phoneRegex.test(changePhoneForm.value.phone)) return uni.showToast({ title: '请输入正确的手机号', icon: 'none'})
 //发送验证码
timerFlag.value=true
 timer=setInterval(() => {
  if(countdown.value===0) {
    timerFlag.value=false
    clearInterval(timer)
    countdown.value=60
    timer = null; 

    return
  }
  countdown.value--
 }, 1000);
 //调用获取验证码api
await getCodeAPI(changePhoneForm.value.phone)

}
//dialog关闭时触发
const closeDialog=()=>{
 showDialog.value=false
}
const stop= watch(showDialog,(newVal)=>{
  if(newVal){
    changePhoneForm.value={phone:'',code:''}
  }
})
//确认修改手机号
const confirm=async()=>{
  if(!phoneRegex.test(changePhoneForm.value.phone)) return uni.showToast({ title: '请输入正确的手机号', icon: 'none'})
  if(!codeRegex.test(changePhoneForm.value.code)) return uni.showToast({ title: '请输入正确的验证码', icon: 'none'})
  //调用API后端验证
const res= await confirmChangePhoneAPI(changePhoneForm.value)
if(res.code===200){
  clearInterval(timer)
  stop()
  uni.switchTab({ url: '/pages/my/my' })
}

}
const userURL=ref('../../../static/user.png')
const iconURL=ref('../../../static/camera.png')
//点击上传头像
const chooseAvatar=(e:any)=>{
  console.log(formData.value.avatarUrl);
formData.value.avatarUrl=e.detail.avatarUrl
}
//保存
const save=async()=>{

  if(formData.value.avatarUrl.startsWith('http://tmp')){
    //说明是临时文件
    uni.uploadFile({
  url: 'http://localhost:8080/upload', 
  filePath: formData.value.avatarUrl,
  name: 'file',
  formData:{
    theme:'avatar'
  },
 async success(res) {
  
  const JSONRes= JSON.parse(res.data)
    formData.value.avatarUrl=JSONRes.data
    //保存到数据库
   await saveUserInfoAPI(formData.value)
   //返回
   uni.navigateBack()
  }
})  
  }else{
    await saveUserInfoAPI(formData.value)
   //返回
   uni.navigateBack()
  }


}
const logout=()=>{
  userStore.clearUserInfo()
  addressStore.clearAddressData()
  shopStore.clearShopData()
  cartStore.clearCartList()
  voucherStore.clearVoucherList()
  uni.clearStorageSync()
  uni.navigateBack()
}
</script>

<style lang="less">
page{
  background-color: #F5F5F5;
}
.avatar{
  border-top: 1rpx #FFFFFF solid;
  height: 400rpx;
   background-color: #FFFFFF;
   .box{
    position: relative;
    margin: 80rpx auto;
    width: 220rpx;
    height: 220rpx;
    .img{
      border: 3rpx solid #F5F5F5;
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }
    .icon{
      position: absolute;
      right: -4rpx;
      bottom: -8rpx;
      width: 55rpx;
      height: 55rpx;
    }
    button::after {
    border: none;
}
   }
}
.infoInp{
  z-index: 1;
  position: relative;
  padding: 40rpx;
  margin-top: 40rpx;
  height: 400rpx;
  background-color: #FFFFFF;
  .uni-forms-item__content{
    margin-top: 15rpx;
  }
  .changePhone{
    z-index: 2;
    right: 40rpx;
    top: 43%  ;
    position: absolute;
    color: #EA0012;
    font-size: 27rpx;
  }
}
.desc{
  margin-top: 10rpx;
  padding: 15rpx;
  display: block;
  color: #AAAAAA;
  font-size: 22rpx;
}
.save{
  z-index: 2;
  margin-top: 30rpx;
  background-color: #E60012;
  color: #FFFFFF;
  border-radius: 15rpx;
  width: 90%;
}
.logout{
  margin-top: 80rpx;
  display: block;
  color: #AAAAB1;
  font-size: 26rpx;
  text-align: center;
}

.dialog{
  border-radius: 15rpx;
  padding: 40rpx;
  z-index: 0;
  position: fixed;
  top: 50%;
  left: 50%;
   transform: translateX(-50%) translateY(-50%);
  opacity: 0;
  transition: all 0.3s;
  width: 80%;
  height: 600rpx;
  background-color: #FFFFFF;
  .toast{
    text-align: center;
    font-size: 35rpx;
    color: #333333;
    font-weight: 500;
  }
  .desc{
    padding: 0;
  margin: 20rpx 0 0 0;
  font-size: 26rpx;
  color: #7F7F7F;
  .software{
    color: #4C4C4C;
  }
  }
  .inp{
    position: relative;
    margin-top: 50rpx;
    input{
      font-size: 28rpx;
      height: 70rpx;
      border-radius: 10rpx;
      border: 1rpx solid #E7E7E7;
      margin: 30rpx 0;
    }
    .getcode{
      z-index: 2;
    position: absolute;
    color: #E60012;
    font-size: 28rpx;
    right: 10rpx;
    bottom: 20rpx;
  }
  .disable-get-code{
    color: #AAAAAA;
  }
  }
  .icon{
    position: absolute;
    right: 0;
    top: 0;
  }
  .confirm{
    margin-top: 80rpx;
    line-height: 90rpx;
    background-color: #E60012;
    color: #FFFFFF;
    height: 90rpx;
  }
 
}
.showDialog{
  z-index: 3;
  opacity: 1;
}

.mask{
  z-index: 2;
  transition: all 0.5s;
 
  opacity: 0;
  height: 100%;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
  background-color: #333333;
}
.showMask{
  opacity: 0.7;
}
</style>
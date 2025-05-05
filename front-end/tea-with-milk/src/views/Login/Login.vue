<script setup lang="ts">
import { ref } from 'vue'
import type {EmployeeLoginForm} from '@/types/Employee'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { employeeLoginAPI } from '@/apis/Employee';
import CryptoJS from 'crypto-js'
import { useEmployeeStore } from '@/stores/Employee';
import { useRouter } from 'vue-router';
import type { BoundingRect } from 'echarts/types/src/util/graphic.js';

const router=useRouter()
const employeeStore=useEmployeeStore()
//鼠标的坐标
const mouseX=ref(0)
const mouseY=ref(0)
// 左眼眼球的位置
const leftEyeballX = ref(0)
const leftEyeballY = ref(0)
// 右眼眼球的位置
const rightEyeballX = ref(0)
const rightEyeballY = ref(0)
const leftEyeRef=ref()
const rightEyeRef=ref()

//监听鼠标移动
const handlerMouseMove=(e:MouseEvent)=>{
    mouseX.value=e.pageX
    mouseY.value=e.pageY
      // 获取左眼和右眼的 DOM 元素
   const leftRect:BoundingRect= leftEyeRef.value.getBoundingClientRect()
   const rightRect=rightEyeRef.value.getBoundingClientRect()
        // 获取左眼和右眼的位置和尺寸
        //左眼
        const leftCenterPointX=leftRect.x+leftRect.width/2
        const leftCenterPointY=leftRect.y+leftRect.height/2
        //右眼
        const rightCenterPointX=rightRect.x+rightRect.width/2
        const rightCenterPointY=rightRect.y+rightRect.height/2
        // 计算鼠标相对于左眼和右眼的位置
        const leftDeltaX = mouseX.value - leftCenterPointX
        const leftDeltaY = mouseY.value - leftCenterPointY
        const rightDeltaX = mouseX.value - rightCenterPointX
        const rightDeltaY = mouseY.value - rightCenterPointY

        // 计算眼球的最大移动距离（半径减去眼球半径）
        const leftEyeRadius = leftRect.width / 2
        const eyeballRadius = 20 // 眼球半径
        const maxDistance = leftEyeRadius - eyeballRadius

        // 计算眼球的移动距离
        const leftDistance = Math.min(Math.sqrt(leftDeltaX * leftDeltaX + leftDeltaY * leftDeltaY), maxDistance)
        const rightDistance = Math.min(Math.sqrt(rightDeltaX * rightDeltaX + rightDeltaY * rightDeltaY), maxDistance)

        // 计算眼球的移动角度
        const leftAngle = Math.atan2(leftDeltaY, leftDeltaX)
        const rightAngle = Math.atan2(rightDeltaY, rightDeltaX)

        // 计算眼球的最终位置
        leftEyeballX.value = Math.cos(leftAngle) * leftDistance
        leftEyeballY.value = Math.sin(leftAngle) * leftDistance
        rightEyeballX.value = Math.cos(rightAngle) * rightDistance
        rightEyeballY.value = Math.sin(rightAngle) * rightDistance
    }
  

//表单输入
const form=ref<EmployeeLoginForm>({
  shopAccount:'mxychxy',
  shopPassword:'123456',
  username:'mxycpwy',
  userPassword:'pwy123456'
})
//表单ref
const formRef=ref<FormInstance>()
//表单校验规则
const rules=ref<FormRules<typeof form>>({
  shopAccount:[
     {
      required:true,
      message:'店铺账户不能为空',
      trigger:'blur'
     }
  ],
  shopPassword:[
     {
      required:true,
      message:'店铺密码不能为空',
      trigger:'blur'
     }
  ],
  username:[
     {
      required:true,
      message:'用户名不能为空',
      trigger:'blur'
     }
  ],
  userPassword:[
    {
      required:true,
      message:'用户密码不能为空',
      trigger:'blur'
     },
     {
      min:6,
      message:'用户密码不能小于6位',
      trigger:'blur'
     }
  ]
})
const login=async()=>{
  try{
    await formRef.value?.validate()
  }catch(err:any){
    return ElMessage.error('请填写完整信息')
  }
  //对店铺密码和用户密码进行加密
  const shopPassword=form.value.shopPassword.trim()
  const userPassword=form.value.userPassword.trim()
  const hashShopPassword=CryptoJS.HmacSHA256(shopPassword, 'mxycSecret')
  const hashUserPassword=CryptoJS.HmacSHA256(userPassword, 'mxycSecret')
 const res:any= await employeeLoginAPI({
    shopAccount:form.value.shopAccount,
    shopPassword:hashShopPassword.toString(CryptoJS.enc.Hex),
    username:form.value.username,
    userPassword:hashUserPassword.toString(CryptoJS.enc.Hex)
  })
  if(res.code===200){
//存储用户信息
employeeStore.setEmployeeInfo(res.data)
//路由到首页
setTimeout(() => {
  router.push('/layout')
}, 1500);
  }
}
</script>

<template>

  <!-- 表单登录部分 -->
   <div class="login" @mousemove="handlerMouseMove">
    <!-- 左侧企业介绍 -->
    <div class="intro">
      <h3 class="main-title title">蜜雪宇城</h3>
      <h5 class="sub-title title">立志打造出第二个高性价比的奶茶王朝</h5>
    </div>
    <!-- 右侧表单区域 -->
    <div class="form">
      <div class="face">
          <!-- 左眼 -->
           <div class="left-eye eyes" ref="leftEyeRef">
              <!-- 眼球 -->
               <div class="eyeball" :style="{ transform: `translate(${leftEyeballX}px, ${leftEyeballY}px)` }"></div>
           </div>
           <!-- 右眼 -->
            <div class="right-eye eyes" ref="rightEyeRef">
              <!-- 眼球 -->
              <div class="eyeball" :style="{ transform: `translate(${rightEyeballX}px, ${rightEyeballY}px)` }"></div>
            </div>
            <!-- 嘴巴 -->
             <div class="mouth">
              
             </div>
      </div>
      <div class="input">
        
        <el-form :model="form" label-width="80px" :rules="rules" ref="formRef" label-position="left">
    <el-form-item label="店铺账户" prop="shopAccount">
      <el-input v-model="form.shopAccount" />
    </el-form-item>

    <el-form-item label="店铺密码" prop="shopPassword">
      <el-input v-model="form.shopPassword" type="password" show-password/>
    </el-form-item>

    <el-form-item label="用户名" prop="username">
      <el-input v-model="form.username" />
    </el-form-item>

    <el-form-item label="用户密码" prop="userPassword">
      <el-input v-model="form.userPassword" type="password" show-password/>
    </el-form-item>
    <!-- 登录按钮 -->
   <el-form-item>
      <el-button type="primary" @click="login">登录</el-button>
    </el-form-item>
    
  </el-form> 
      </div>
   <!-- 注意事项 -->
    <div class="tips">
      <span class="title">注:</span>
      <ul>
        <li>1.请勿泄露自己的个人账户以及门店账户</li>
        <li>2.每个人对应自己的门店，请填写自己门店的账户</li>
        <li>3.如若忘记密码，请联系管理员重新创建账户</li>
      </ul>
    </div>
    </div>
   
  </div> 
 
  
</template>

<style scoped lang="less">

.login{
  height: 100%;
  display: flex;
  .intro{
    border-top-right-radius: 10px;
    border-bottom-right-radius: 10px;
    box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.5);
    width: 45%;
    background: url('../../assets/logo.png') center center  ;
    .title{
     
   display: inline-block;
   margin: 20px 5px 0 20px;
}
.main-title{
  font-weight: bold;
}
.sub-title{
  color: #938c8c;
  letter-spacing: 2px;
}

  }
  .form{
    display: flex;
    flex-direction: column;
    flex: 1;
    .face {
    position: relative;
    margin: 0 auto;
    width: 300px;
    border-radius: 50%;
    background-color: #ffd700; /* 将脸的颜色改为黄色 */
    height: 300px;
    box-shadow: 0 5px 10px rgba(0, 0, 0, 0.3); /* 添加阴影效果 */
   .left-eye {
      position: relative;
      left: 40px;
    }
   .right-eye {
      position: relative;
      right: 40px;
    }
   .eyes {
      position: absolute;
      top: 75px;
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background-color: #00bfff; /* 将眼睛颜色改为天蓝色 */
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); /* 为眼睛添加小阴影 */
     .eyeball {
       margin: 20px auto;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: #ffffff; /* 眼球颜色改为白色 */
      }
    }
   .mouth {
      position: absolute;
      bottom: 25px;
      left: 50%;
      transform: translateX(-50%);
      width: 200px;
      height: 100px;
      background-color: #ff6347; /* 将嘴巴颜色改为珊瑚红 */
      border-bottom-left-radius: 100px;
      border-bottom-right-radius: 100px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); /* 为嘴巴添加小阴影 */
    }
  }
  .input{
    margin-top: 40px;
    padding-left: 40px;
   .el-input{
    width: 400px;
   }
  
  }
  .tips{
    box-shadow: 0 -5px 5px rgba(0, 0, 0, 0.5);
    border-radius: 8px;
    flex: 1;
    background-image: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%); 
    border-top: 1px solid rgba(0, 0, 0, 0.5);
    .title{
      display: inline-block;
      margin: 20px 5px 0 20px;
      font-weight: bolder;
      color: #938c8c;
      letter-spacing: 2px;
      font-size: 28px;
      font-style: italic;
    }
    ul{
      margin-left: 20px;
      li {
      position: relative;
      margin-bottom: 10px;
      font-size: 24px;
      color: #555;
      line-height: 1.5;
      border-radius: 8px;
      transition: color 0.3s ease;
      
      &:hover {
        color: #007bff; 
      }
    }
  }
  }
  
}
}
</style>
<script lang="ts" setup>
import { uploadImgAPI } from '@/apis/common/upload';
import { getShopInfoAPI,updateShopInfoAPI,updateShopPasswordAPI } from '@/apis/Shop';
import { getEmployeeListAPI,getPrsonInfoAPI,setPasswordAPI,setPersonInfoAPI,addEmployeeAPI,delEmployeeByIdAPI } from '@/apis/Employee';
import type { ShopInfo } from '@/types/Shop';
import type {EmployeeType} from '@/types/Employee'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { ref,onMounted } from 'vue';
import { useEmployeeStore } from '@/stores/Employee';
import { useRouter } from 'vue-router'
const router=useRouter()
import CryptoJS from 'crypto-js'
//控制修改店铺密码的弹窗的显示与隐藏
const dialogVisible=ref(false)
const employeeStore=useEmployeeStore()
//是否禁用修改
const DisabledUpdate=ref(true)
//存储门店信息
const shopData=ref<ShopInfo>({
  id:'',
  name:'',
  longitude:0,
  latitude:0,
  position:'',
  account:'',
  password:'',
  phone:'',
  logo:''
})
interface EmployeeFormType{
   username:string,
   password:string,
   name:string,
   phone:string
}
//存储添加员工form
const EmployeeForm=ref<EmployeeFormType>({
  username:'',
   password:'',
   name:'',
   phone:''
})
interface ChangePasswordType{
  originalPassword:string,newPassword:string,confirmPassword:string
}
//修改密码表单
const changePasswordForm=ref<ChangePasswordType>({
  originalPassword:'',
  newPassword:'',
  confirmPassword:''
})
//门店表单校验flag
const formValidateFlag = ref<{
  [key: string]: boolean;
  shopNameFlag: boolean;
  // shopPasswordFlag: boolean;
  longitudeAndlatitudeFlag: boolean;
  positionFlag: boolean;
  phoneFlag: boolean;
}>({
  shopNameFlag: false,
  shopPasswordFlag: false,
  longitudeAndlatitudeFlag: false,
  positionFlag: false,
  phoneFlag: false,
});
//校验门店名称
const checkedShopName=()=>{
if(shopData.value.name===''){
  formValidateFlag.value.shopNameFlag=true
} else{
  formValidateFlag.value.shopNameFlag=false
}
}
//校验门店密码
const checkedShopPassword=()=>{
  if(shopData.value.password===''|| shopData.value.password.length<6){
    formValidateFlag.value.shopPasswordFlag=true
  } else{
    formValidateFlag.value.shopPasswordFlag=false
  }
}
const longitudeRegex=/^-?((1[0-7]\d|0?\d{1,2})\.(\d{6}))$|^-?180\.(\d{6})$/
const latitudeRegex=/^-?(([1-8]?\d|0?\d)\.(\d{6}))$|^-?90\.(\d{6})$/
//校验经纬度
const checkedLongitudeAndLatitude=()=>{
 if(!longitudeRegex.test(shopData.value.longitude.toString())||!latitudeRegex.test(shopData.value.latitude.toString())){
  formValidateFlag.value.longitudeAndlatitudeFlag=true
 
 }else{
  formValidateFlag.value.longitudeAndlatitudeFlag=false
 
 }
}
//校验门店位置
const checkShopPosition=()=>{
  if(shopData.value.position===''){
    formValidateFlag.value.positionFlag=true
  } else{
    formValidateFlag.value.positionFlag=false

  }
}
const phoneRegex=/^1[3456789]\d{9}$/
//校验手机号
const checkPhone=()=>{
if(!phoneRegex.test(shopData.value.phone)){
  formValidateFlag.value.phoneFlag=true
 } else{
  formValidateFlag.value.phoneFlag=false
 }
}

//获取门店信息
const getShopInfo=async ()=>{
 const res=await getShopInfoAPI()
  shopData.value=res.data
}
//存储员工列表数据
const employeeList=ref<EmployeeType[]>([])
//获取员工列表
const getEmployeeInfo=async ()=>{
 const res=await getEmployeeListAPI()
 employeeList.value=res.data
}
//获取个人信息
const getPersonInfo=async()=>{
  const res=await getPrsonInfoAPI()
  EmployeeForm.value=res.data
}
onMounted(()=>{
  getShopInfo()
  if(employeeStore.employeeInfo?.identify===0){
    getEmployeeInfo()
  }else{
    getPersonInfo()
  }

})
const isSwitchOn=ref(false)
//开启修改门店信息
const openUpdate=(e:MouseEvent)=>{
  //检查该用户是否具有修改门店信息的权限
  if(employeeStore.employeeInfo?.identify!==0){
    ElMessage.error('您没有权限修改门店信息')
     // 阻止switch进入选中状态
     e.preventDefault()
    return
  }
  isSwitchOn.value=true
  DisabledUpdate.value=false
}
//修改店铺密码
const changeShopPassword=async ()=>{ 
dialogVisible.value=true
}
//修改店铺信息
const changeShopInfo=async ()=>{
  for(let key in formValidateFlag.value){
    if(formValidateFlag.value[key]) return ElMessage.error('请填写完整信息')
  }
const res:any= await updateShopInfoAPI(shopData.value)
if(res.code===200){
  //重新获取店铺信息
  getShopInfo()

}

}
const selectFile=ref<File>()
//预览logo
const onSelectImg=async(e:Event)=>{
  //图片预览
  const target=e.target as HTMLInputElement
  const reader = new FileReader();
  const file = target.files![0]
  selectFile.value=file
  reader.readAsDataURL(file)
  reader.onload = function () {
    shopData.value.logo=reader.result as string
  } 
}
//上传logo
const upload=async()=>{
  if(!selectFile.value) return
  const formData=new FormData()
  formData.append('file',selectFile.value)
  const res=await uploadImgAPI(formData,'logo')
  shopData.value.logo=res.data
  console.log(shopData.value.logo);
  
}

const drawerVisible=ref(false)
//密码使用默认值
const useDefault=()=>{
 EmployeeForm.value.password='pwy123456'
}
//清空employeeForm
const clearEmployeeForm=()=>{
  EmployeeForm.value.username=''
  EmployeeForm.value.password=''
  EmployeeForm.value.name=''
  EmployeeForm.value.phone=''
}
//取消
const cancel=()=>{
  drawerVisible.value=false
  clearEmployeeForm()
}
//控制动画的播放
const shakeActive=ref(false)
//开启动画
const startAnimation=()=>{
  shakeActive.value=true
  setTimeout(() => {
    shakeActive.value=false
  }, 500);
}
//新增员工
const save=async ()=>{
 if(!EmployeeForm.value.username){
    ElMessage.warning('员工用户名不能为空')
    startAnimation()
    return
  }
  if(!EmployeeForm.value.password){
  ElMessage.warning('员工密码不能为空')
  startAnimation()
  return
 }
 if(!EmployeeForm.value.name){
  ElMessage.warning('员工姓名不能为空')
  startAnimation()
  return
 }

 if(EmployeeForm.value.password.length<6){
  ElMessage.warning('员工密码必须大于等于6位')
  startAnimation()
  return
 }
 if(!phoneRegex.test(EmployeeForm.value.phone)){
  ElMessage.warning('请输入合法的手机号格式')
  startAnimation()
  return
 }
 EmployeeForm.value.password=CryptoJS.HmacSHA256(EmployeeForm.value.password.trim(), 'mxycSecret').toString(CryptoJS.enc.Hex)
const res:any= await addEmployeeAPI(EmployeeForm.value)
if(res.code===200){
  getEmployeeInfo()
}

 clearEmployeeForm()
}
//删除员工
 const delEmployee=async(id:string)=>{
  //弹出确认框
  try{
    await ElMessageBox.confirm('确定删除该员工吗？','提示',{
    confirmButtonText:'确定',
    cancelButtonText:'取消',
    type:'warning'
  })
  }catch(err){
    console.log('点击了取消');
    return
  }
const res:any= await delEmployeeByIdAPI(id)
 if(res.code===200){
  getEmployeeInfo()

 }

  
}
//修改个人信息
const savePersonInfo=async()=>{
  if(!EmployeeForm.value.name){
    ElMessage.warning('员工姓名不能为空')
    return
  }
  if(!phoneRegex.test(EmployeeForm.value.phone)){
    ElMessage.warning('请输入合法的手机号格式')
    return
  }
  await setPersonInfoAPI({name:EmployeeForm.value.name,phone:EmployeeForm.value.phone})
  getPersonInfo()
}
const changePasswordFormRef=ref<FormInstance>()
//控制修改密码盒子的过渡效果
const passwordBoxActive=ref(false)
//清空修改密码表单
const clearChangePasswordForm=()=>{
  changePasswordForm.value.originalPassword=''
  changePasswordForm.value.newPassword=''
  changePasswordForm.value.confirmPassword=''
}
//取消修改密码
const cancelChangePassword=()=>{
  dialogVisible.value=false
  changePasswordFormRef.value?.resetFields()
  passwordBoxActive.value=false
  clearChangePasswordForm()
}
//确认修改店铺密码
const confirmChangeShopPassword=async()=>{
  const validate= await changePasswordFormRef.value!.validate()
  if(validate){
    const orginalPasswordTrim=changePasswordForm.value.originalPassword.trim()
  const newPasswordTrim=changePasswordForm.value.newPassword.trim()
  const hashedOriginalPassword= CryptoJS.HmacSHA256(orginalPasswordTrim, 'mxycSecret').toString(CryptoJS.enc.Hex)
  const hashedNewPassword= CryptoJS.HmacSHA256(newPasswordTrim, 'mxycSecret').toString(CryptoJS.enc.Hex)
 await updateShopPasswordAPI({ originalPassword:hashedOriginalPassword, newPassword:hashedNewPassword  })
 dialogVisible.value=false
 clearChangePasswordForm()

  }

  
}
//确认修改密码
const confirmChangePassword=async()=>{
 const validate= await changePasswordFormRef.value!.validate()
 if(validate){
    const orginalPasswordTrim=changePasswordForm.value.originalPassword.trim()
    const newPasswordTrim=changePasswordForm.value.newPassword.trim()
   const hashedOriginalPassword= CryptoJS.HmacSHA256(orginalPasswordTrim, 'mxycSecret').toString(CryptoJS.enc.Hex)
   const hashedNewPassword= CryptoJS.HmacSHA256(newPasswordTrim, 'mxycSecret').toString(CryptoJS.enc.Hex)
  await setPasswordAPI({ originalPassword:hashedOriginalPassword, newPassword:hashedNewPassword  })
  //能够执行到这里说明密码修改成功了
  //清楚用户数据,跳转到登录页
  employeeStore.clearEmployeeInfo()
  setTimeout(() => {
    router.replace('/')
  }, 1500);
 }
 
}
const rules=ref<FormRules<ChangePasswordType>>({
  originalPassword:[
  {
    required:true,
    message:'请输入原始密码',
    trigger:'blur'
  },
  {
    min:6,
    message:'密码长度不能小于6位',
    trigger:'blur'
  }
  ],
  newPassword:[
  {
    required:true,
    message:'请输入新密码',
    trigger:'blur'
  },
  {
    min:6,
    message:'密码长度不能小于6位',
    trigger:'blur'
  }
  ],
  confirmPassword:[
  {
    required:true,
    message:'请输入确认密码',
    trigger:'blur'
  },
  {
    validator:(rule,value,callback)=>{
      if(value!==changePasswordForm.value.newPassword){
        return callback(new Error('两次密码不一致'))
      }else{
        return callback()
      }
    }
  }
  ]
})
</script>
<template>
  <div class="content">
    <!-- 左边门店部分 -->
    <div class="shop">
      <img :src="shopData.logo" alt="" class="logo">
      <div class="input-group mb-3">
  <input type="file" class="form-control" id="inputGroupFile02" :disabled="true" @change="onSelectImg">
  <el-popover 
   placement="top-start"
    title="温馨提示"
    :width="200"
    trigger="hover"
    content="很抱歉,暂不支持修改企业logo"
    >
    <template #reference>
      <button class="btn btn-outline-secondary" type="button" id="button-addon2" @click="upload">上传</button>
    </template>
 
  </el-popover>

</div>
      <!-- 表单部分 -->
      <form>
        <!-- 门店名称 -->
  <div class="mb-3">
    <label for="shop-name" class="form-label">门店名称</label>
    <input type="text" class="form-control" id="shop-name" aria-describedby="emailHelp" :disabled="DisabledUpdate" v-model="shopData.name" @input="checkedShopName">
    <!-- 表单验证 -->
    <div class="invalid-feedback" v-show="formValidateFlag.shopNameFlag">
          门店名称不能为空
        </div>
  </div>

  <div class="mb-3">
      <!-- 门店账号 -->
    <div class="col-auto">
      <label for="shop-account" class="form-label fixed-label-width">门店账号</label>
  </div>
  <div class="col-auto">
    <input type="text" class="form-control" id="shop-account" disabled :value="shopData.account" >
  </div>
   <!-- 表单验证 -->
   <div class="invalid-feedback" v-show="formValidateFlag.shopPasswordFlag">
          门店密码必须大于6位
        </div>
  </div>

    <!-- 经纬度-->
  <div class="row mb-3 align-items-center">
  <div class="col-auto">
    <label for="shop-longitude" class="col-form-label fixed-label-width">经度</label>
  </div>
  <div class="col-auto">
    <input type="text" id="shop-longitude" class="form-control" aria-describedby="passwordHelpInline" :disabled="DisabledUpdate" v-model="shopData.longitude" @input="checkedLongitudeAndLatitude">
  </div>
  <div class="col-auto">
    <label for="shop-latitude" class="col-form-label fixed-label-width">纬度</label>
  </div>
  <div class="col-auto">
    <input type="text" id="shop-latitude" class="form-control" aria-describedby="passwordHelpInline" :disabled="DisabledUpdate" v-model="shopData.latitude"  @input="checkedLongitudeAndLatitude">
  </div>
   <div class="col-auto">
    <span class="form-text">
      经纬度需要保留6位小数
    </span>
  </div>
 
 <!-- 表单验证 -->
 <div class="invalid-feedback" v-show="formValidateFlag.longitudeAndlatitudeFlag">
          请输入合法的经纬度
        </div>
 
</div>

<!-- 门店位置 -->
<div class="mb-3">
  <label for="shop-place" class="form-label">门店位置</label>
  <input type="text" class="form-control" placeholder="门店位置" aria-label="Username" aria-describedby="basic-addon1" id="shop-place" :disabled="DisabledUpdate" v-model="shopData.position" @input="checkShopPosition">
   <!-- 表单验证 -->
 <div class="invalid-feedback" v-show="formValidateFlag.positionFlag">
          门店位置填写不能为空
        </div>
</div>
<!-- 联系电话 -->
<div class="mb-3">
  <label for="contact-number" class="form-label">联系电话</label>
  <input type="text" class="form-control" placeholder="联系电话" aria-label="Username" id="contact-number" :disabled="DisabledUpdate" v-model="shopData.phone" @input="checkPhone">
   <!-- 表单验证 -->
 <div class="invalid-feedback" v-show="formValidateFlag.phoneFlag">
          请输入合法的手机号
        </div>
</div>
<div class="form-check form-switch">
  <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault" v-model="isSwitchOn" :disabled="isSwitchOn"  @click="openUpdate($event)" >
  <label class="form-check-label" for="flexSwitchCheckDefault" @click="openUpdate($event)">解锁</label>
</div>
<div class="mb-3">
    <button type="button" class="btn btn-outline-success" v-if="isSwitchOn" @click="changeShopInfo">确认修改</button>
    &nbsp;
    <button type="button" class="btn btn-outline-danger" v-if="isSwitchOn" @click="changeShopPassword">修改密码</button>


</div>
</form>
    </div>
    <!-- 右边员工部分 -->
    <div class="employee">
      <!-- 管理员可以看到的 -->
       <div class="admin" v-if="employeeStore.employeeInfo?.identify===0">
        <h4>我的员工</h4>
    <!-- 表格部分 -->
     <div class="my-employee">
      <button class="add-employee" @click="drawerVisible=true">添加员工</button>
      <table>
      <thead>
        <tr>
        <th>用户名</th>
        <th>姓名</th>
        <th>手机号</th>
        <th>操作</th>
      </tr>
      </thead>
     <tbody>
      <tr v-for="item in employeeList" :key="item.id">
        <td>{{item.username}}</td>
        <td>{{item.name}}</td>
        <td>{{item.phone}}</td>
        <td><button class="del-employee" @click="delEmployee(item.id)">删除</button></td>
      </tr>
     </tbody>
     </table>
     </div>
     <!-- 添加员工抽屉 -->
      <div class="add-drawer" :class="{'drawerActive':drawerVisible}">
        <div class="in" :class="{'shakeActive':shakeActive}">
          <form>
            <div class="form-item">
              <label for="username">用户名</label>
              <input type="text" id="username" placeholder="请输入用户名" v-model="EmployeeForm.username">
            </div>
            <div class="form-item">
              <label for="password">密码</label>
              <input type="text" id="password" placeholder="请输入密码" v-model="EmployeeForm.password">
              <button type="button" class="use-default" @click="useDefault">使用默认值</button>
            </div>
            <div class="form-item">
              <label for="name">姓名</label>
              <input type="text" id="name" placeholder="请输入姓名" v-model="EmployeeForm.name">
            </div>
            <div class="form-item">
              <label for="phone">电话</label>
              <input type="text" id="phone" placeholder="请输入电话" v-model="EmployeeForm.phone">
            </div>
            <div class="form-item operate">
              <button type="button" class="cancel" @click="cancel">取消</button>
              <button type="button" class="save" @click="save">保存</button>
            </div>
          </form>
        </div>
      </div>
       </div>
      <!-- 普通员工看到的 -->
       <div class="ordinary-employee"v-else>
        <h4>我的信息</h4>
        <div class="form">
          <div class="form-item">
            <label for="username">用户名</label>
            <input type="text" id="username" placeholder="请输入用户名" :value="EmployeeForm.username" disabled>
          </div>
          
          <div class="form-item">
            <label for="name">姓名</label>
            <input type="text" id="name" placeholder="请输入姓名" v-model="EmployeeForm.name">
          </div>
          <div class="form-item">
            <label for="phone">电话</label>
            <input type="text" id="phone" placeholder="请输入电话" v-model="EmployeeForm.phone">
          </div>
          <div class="form-item">
            <button type="button" @click="savePersonInfo">保存</button>
          </div>
          <!-- 修改密码 -->
          <div class="form-item">
            <button type="button" class="change-password" @click="passwordBoxActive=true">修改密码</button>
          </div>
        </div>
        <!-- 密码修改 -->
         <div class="password-box" :class="{'password-box-active':passwordBoxActive}">
          <el-form :model="changePasswordForm" label-width="80px" :rules="rules" ref="changePasswordFormRef">
             <el-form-item label="原密码" prop="originalPassword">
              <el-input v-model="changePasswordForm.originalPassword" type="password" show-password/>
             </el-form-item>
             <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="changePasswordForm.newPassword"  type="password" show-password/>
             </el-form-item>
             <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="changePasswordForm.confirmPassword"  type="password" show-password/>
             </el-form-item>
             <el-form-item>
              <el-button type="primary" @click="cancelChangePassword">取消</el-button>
              <el-button color="#9197F4" @click="confirmChangePassword">确认</el-button>
             </el-form-item>
          </el-form>
         </div>
       </div>
    </div>
  </div>
<!-- 修改店铺密码dialog -->
<el-dialog
    v-model="dialogVisible"
    title="Tips"
    width="500"
  >
  <el-form :model="changePasswordForm" label-width="80px" :rules="rules" ref="changePasswordFormRef">
             <el-form-item label="原密码" prop="originalPassword">
              <el-input v-model="changePasswordForm.originalPassword" type="password" show-password/>
             </el-form-item>
             <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="changePasswordForm.newPassword"  type="password" show-password/>
             </el-form-item>
             <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="changePasswordForm.confirmPassword"  type="password" show-password/>
             </el-form-item>
             <el-form-item>
              <el-button type="primary" @click="cancelChangePassword">取消</el-button>
              <el-button color="#9197F4" @click="confirmChangeShopPassword">确认</el-button>
             </el-form-item>
          </el-form>
  </el-dialog>
</template>

<style scoped lang="less">
.content{
  display: flex;
  height: 100%;
 
  .shop{
    width: 50%;
    border-right: 1px solid #ccc;
    padding: 20px;
    .logo{
      width: 180px;
      height: 180px;
      border: 1px solid black;
      margin: 0 auto 20px;
    }
    .getPosition{
      margin-top: 5px;
    }
    .fixed-label-width{
      width: 80px;
    }
    .invalid-feedback{
      display: block;
    }
    
  }
  .employee{
    flex: 1;
    border-left: 1px solid #ccc;
    //管理员
    .admin{
      h4{
      padding: 10px 10px 0;
    }
    .my-employee {
      table{
            width: 80%;
            margin: 0 auto;
            border-collapse: collapse;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            background-color: white;
            
      }
            max-height: 500px;
            overflow: scroll;
            &:hover{
              cursor:default;
            }
            th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            position: sticky;
            top: 0;
            background-color: #007BFF;
            color: white;
        }

        tr:hover {
            background-color: #f5f5f5;
        }
        .add-employee{
          border: none;
          width: 80px;
          height: 40px;
          border-radius: 5px;
          background-image: linear-gradient(to right, #a8cadd, #4cbff0);
          margin: 15px;
          color: #FFFFFF;
          &:hover{
            opacity: 0.9;
          }
        }
        .del-employee{
          border: none;
          width: 50px;
          height: 30px;
          border-radius: 5px;
          background-color: rgb(198, 204, 205);
          &:hover{
            background-color: lightblue;
          }
        }
        }
    .add-drawer{
      opacity: 0;
      transition: all 0.7s;
      transform: translateX(100%) translateY(100%);
      margin-top: 20px;
      height: 300px;
      padding: 30px;
      .in{
        background-color: #e6f7ff; 
        padding: 10px;
        width: 85%;
        margin: 0 auto;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
        height: 100%;
        border-radius: 10px;
        form{
          .form-item{
            margin: 10px 0;
            button{
              font-size: 15px;
              color: #FFFFFF;
              width: 100px;
              height: 35px;
              border: none;
              border-radius: 5px;
              letter-spacing: 1px;
              &:hover{
              opacity: 0.9;
             }
            }
            input{
              border: none;
              height: 30px;
              border-radius: 5px;
              &:focus{
                outline: none;
                border-color: #007BFF; // 自定义边框颜色
                box-shadow: 0 0 5px rgba(0, 123, 255, 0.5); // 添加柔和的阴影
              }
            }
            .use-default{
              background-image: linear-gradient(to right, #89656b, #ff7eb3);
              margin-left: 10px;
            
            }
            label{
              width: 80px;
            }
            input{
              width: 350px;

            }
            &.operate{
              margin-top: 15px;
              display: flex;
              justify-content: space-around;
             .cancel{
               background-image: linear-gradient(to right, #86898b, #77a7c2);
             }
             .save{
              background-image: linear-gradient(to right, #c4dda8, #adb83b);
             }
            }
          }
        }
      }
      &.drawerActive{
        transform: translateX(0);
       opacity: 1;
      }
    }

    }
  //普通员工
  .ordinary-employee{
    h4{
      margin: 15px;
    }
    .form{
      background-color: #F5F5F5;
      padding: 10px;
      .form-item{
          margin: 10px;
          label{
            width: 100px;
          }
          input{
            width: 300px;
              border: none;
              height: 30px;
              border-radius: 5px;
              &:focus{
                outline: none;
                border-color: #007BFF; // 自定义边框颜色
                box-shadow: 0 0 5px rgba(0, 123, 255, 0.5); // 添加柔和的阴影
              }
            }
            button{
              background-image: linear-gradient(to right, #c4dda8, #adb83b);
              font-size: 15px;
              color: #FFFFFF;
              width: 100px;
              height: 35px;
              border: none;
              border-radius: 5px;
              letter-spacing: 1px;
              &:hover{
              opacity: 0.9;
             }
             &.change-password{
      background-image: linear-gradient(to right, #5ba671, #5f99c9);
    }
      }
    }
   
  }
  .password-box{
    transition: all 0.6s;
    transform: scale(0);
    margin-top: 40px;
    height: 300px;
    .el-input{
      width: 400px;
    }
    &.password-box-active{
      transform: scale(1) rotateX(360deg);
    }
  }
  }
  }
}
//振动动画
@keyframes shake {
      0% {
        transform: translateX(0);
      }

      20% {
        transform: translateX(-10px) translateY(-10px);
      }

      40% {
        transform: translateX(10px) translateY(10px);
      }

      60% {
        transform: translateX(-10px) translateY(-10px);
      }

      80% {
        transform: translateX(10px) translateY(10px);
      }

      100% {
        transform: translateX(0);
      }
    }
  //开启动画
  .shakeActive{
    animation: shake 0.5s infinite ;
  }
</style>
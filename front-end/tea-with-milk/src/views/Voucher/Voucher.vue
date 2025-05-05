<script lang="ts" setup>
import {addVoucherAPI, getVoucherListAPI,editVoucherAPI,getVoucherListByConditionAPI} from "@/apis/Voucher";
import type { Voucher } from "@/types/Voucher";
import {ref,onMounted, watch} from "vue";
import { Edit } from "@element-plus/icons-vue";
import { uploadImgAPI } from "@/apis/common/upload";
import { ElMessage } from "element-plus";
const voucherList=ref<Voucher[]>([])
//控制弹出层的显示与隐藏
const dialogVisible=ref(false)
//获取所有优惠券
const getVoucherList=async()=>{
   const res=await getVoucherListAPI()
  voucherList.value=res.data
  voucherList.value.forEach(item=>{
    
    item.type=typeOptions.find(f=>f.value===item.type.toString())!.label
     const now=new Date().toISOString();
     if(item.expirationTime&&item.expirationTime.toString()<now){
      item.status='已过期'
     }else{
      item.status='生效期'
     }
  })
  
}
//折扣率，满减，直减正则
const regexArr=[/^0\.[0-9]$/,/^[1-9]+$/]
//优惠券类型选择
const typeOptions=[
  {
    label: '直减型',
    value:'0'
  },
  {
    label:'满减型',
    value:'1'
  },
  {
    label:'折扣型',
    value:'2'
  }
]
onMounted(()=>{
  getVoucherList()
})
const form=ref<{name:string,startTime:string,endTime:string,type:string}>(
  {name:'',startTime:'',endTime:'',type:''}
)
//编辑或者添加优惠券的form
interface operateType{
  id?:string,name:string,startTime:string,endTime?:string,url:string,type:string,discountRate?:string,deduct?:string,threshold?:string,balance:number,coinNeed:number
}
const operateForm=ref<operateType>({
  id:'',
  name:'',
  startTime:'',
  endTime:'',
  url:'',
  type:'',
  discountRate:'',
  deduct:'',
  threshold:'',
  balance:0,
  coinNeed:0
} )
const inputRef=ref<HTMLInputElement>()
//添加优惠券
const addVoucher= ()=>{
  dialogVisible.value=true

}
const uploadImg=()=>{
inputRef.value!.click()
}
//点击上传图片时触发
const onSelectImg=async(e:Event)=>{
  const target=e.target as HTMLInputElement
  if(target.files!.length===0){
    return console.log('未选择文件');
  }
  const file=target.files![0]
  const formData=new FormData()
  formData.append('file',file)
const res= await uploadImgAPI(formData,'voucher')
operateForm.value.url=res.data

}
//清空operate表单数据
const clearOperateForm=()=>{
  operateForm.value={
    id:'',
    name:'',
    startTime:'',
    endTime:'',
    url:'',
    type:'',
    discountRate:'',
    deduct:'',
    threshold:'',
    balance:0,
    coinNeed:0
  }
}
//清空form表单数据
const clearForm=()=>[
  form.value={
    name:'',
    startTime:'',
    endTime:'',
    type:''
  }
]
watch(dialogVisible,(newValue)=>{
  if(!newValue) clearOperateForm()
})

const onDiscountRateBlur=()=>{
  if(!regexArr[0].test(operateForm.value.discountRate!)) return ElMessage.warning('请输入正确的折扣率!(0,1)')
}
const onAmountBlur=(num?:string)=>{
  if(!num||!regexArr[1].test(num)) return ElMessage.warning('请输入正确的金额')
}
//修改或者新增
const confirm=async ()=>{
  console.log(operateForm.value.endTime);
  if(!operateForm.value.startTime) return ElMessage.warning('请选择生效日期')
  if(operateForm.value.endTime!== null && operateForm.value.endTime!== ''&&operateForm.value.startTime>operateForm.value.endTime!) return ElMessage.warning('截止日期不能小于生效日期')
  if(!operateForm.value.url) return ElMessage.warning('请上传优惠券图片')
  let data:Voucher;
  if(!operateForm.value.id){
   //没有id,则是新增操作
   data={
    name:operateForm.value.name,
    url:operateForm.value.url,
    discountRate:operateForm.value.discountRate,
    deduct:operateForm.value.deduct,
    threshold:operateForm.value.threshold,
    effectiveTime:new Date(operateForm.value.startTime),
    number:operateForm.value.balance,
    type:operateForm.value.type,
    coinNeed:operateForm.value.coinNeed
   }
   if(operateForm.value.endTime) data.expirationTime=new Date(operateForm.value.endTime)
   await addVoucherAPI(data)
  }else{
    //修改操作
    data={
      id:operateForm.value.id,
      name:operateForm.value.name,
      url:operateForm.value.url,
      discountRate:operateForm.value.discountRate,
      deduct:operateForm.value.deduct,
      threshold:operateForm.value.threshold,
      effectiveTime:new Date(operateForm.value.startTime),
      number:operateForm.value.balance,
      type:operateForm.value.type,
      coinNeed:operateForm.value.coinNeed
    }
    if(operateForm.value.endTime) data.expirationTime=new Date(operateForm.value.endTime)
    await editVoucherAPI(data)
  }
 dialogVisible.value=false
  getVoucherList()
}
//删除图片
const delImg=()=>{
  operateForm.value.url=''
}
//编辑
const edit=(row:Voucher)=>{
  operateForm.value={
    id:row.id,
    name:row.name,
    startTime:row.effectiveTime.toString(),
    endTime:row.expirationTime?.toString(),
    url:row.url,
    type:typeOptions.find(f=>f.label===row.type)!.value,
    discountRate:row.discountRate,
    deduct:row.deduct,
    threshold:row.threshold,
    balance:row.number,
    coinNeed:row.coinNeed
  }
  dialogVisible.value=true
}
//查询
const search=async()=>{
 const res=await getVoucherListByConditionAPI(form.value)
 voucherList.value=res.data
 voucherList.value.forEach(item=>{
    
    item.type=typeOptions.find(f=>f.value===item.type.toString())!.label
     const now=new Date().toISOString();
     if(item.expirationTime&&item.expirationTime.toString()<now){
      item.status='已过期'
     }else{
      item.status='生效期'
     }
  })
 
}
</script>

<template>
<!-- 表单部分 -->
 <el-form :model="form" :inline="true">
  <el-form-item label="优惠券名称">
    <el-input v-model="form.name" />
  </el-form-item>
  <!-- 日期选择 -->
  <el-form-item label="生效日期">
      <el-date-picker
        v-model="form.startTime"
        type="date"
        placeholder="起始日期"
        format="YYYY-MM-DD"
       value-format="YYYY-MM-DD"
      />
  </el-form-item>
  <el-form-item label="截止日期">
      <el-date-picker
        v-model="form.endTime"
        type="date"
       placeholder="结束日期"
        format="YYYY-MM-DD"
       value-format="YYYY-MM-DD"
      />
  </el-form-item>
  <!-- 类型选择 -->
   <el-form-item label="优惠券类型">
    <el-select
      v-model="form.type"
      placeholder="请选择"
      size="large"
      style="width: 240px"
      clearable
    >
      <el-option
        v-for="item in typeOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
   </el-form-item>
   <!-- 查询 -->
    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button type="info" @click="clearForm()">清空</el-button>
    </el-form-item>
 </el-form>
 <div class="add">
  <el-button @click="addVoucher">添加优惠券</el-button>
 </div>
<!-- 表格区域 -->
<el-table :data="voucherList" :border="true" style="width: 100%">
    <el-table-column prop="name" label="名称" width="180" />
    <el-table-column prop="type" label="类型" width="180" />
    <el-table-column prop="status" label="状态" width="180"/>
    <el-table-column prop="coinNeed" label="宇王币" width="180"/>
    <el-table-column prop="number" label="库存" width="180"/>
    <el-table-column  label="编辑">
      <template #default="scope">
        <el-button type="primary" :icon="Edit" circle @click="edit(scope.row)"/>
      </template>
    
    </el-table-column>
  </el-table>
  <!-- 弹出框 -->
  <el-dialog
    v-model="dialogVisible"
    title="Tips"
    width="500"
  >
  <!-- dialog内容部分 -->
   <!-- 图片部分 -->
   <div class="img" @click="uploadImg">
   <img :src="operateForm.url" alt="" v-if="operateForm.url">
   <span v-else>+</span>
   <input type="file" hidden ref="inputRef" @change="onSelectImg"/>
   <el-icon size="20" v-if="operateForm.url" @click.stop=delImg><CircleCloseFilled /></el-icon>
   </div>
  <el-form :model="operateForm">
    <el-form-item label="名称">
      <el-input v-model="operateForm.name"></el-input>
    </el-form-item>
    <el-form-item label="有效期选择">
      <el-date-picker
        v-model="operateForm.startTime"
        type="date"
        placeholder="起始日期"
      
        format="YYYY-MM-DD"
       value-format="YYYY-MM-DD"
      />
     
    </el-form-item>
    <el-form-item label="截止日期">
      <el-date-picker
        v-model="operateForm.endTime"
        type="date"
        placeholder="截止日期"
        format="YYYY-MM-DD"
       value-format="YYYY-MM-DD"
      />
    </el-form-item>
  
    <el-form-item label="类型">
      <el-select
      v-model="operateForm.type"
      placeholder="请选择"
      size="large"
      style="width: 240px"
    >
      <el-option
        v-for="item in typeOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
    </el-form-item>
    <!-- 如果选择的类型是折扣型，则展示这一栏 -->
    <el-form-item v-if="operateForm.type==='2'" label="折扣率">
      <input type="text" v-model="operateForm.discountRate" @blur="onDiscountRateBlur">
    </el-form-item>
    <!-- 如果选择的类型是满减型，则展示这一栏 -->
     <el-form-item label="门槛" v-if="operateForm.type==='1'">
      <input type="text" v-model="operateForm.threshold" @blur="onAmountBlur(operateForm.threshold)"/>
     </el-form-item>
     <!-- 如果选择的类型是直减型或者满减型，则展示这一栏 -->
      <el-form-item label="减免金额" v-if="operateForm.type==='0'||operateForm.type==='1'">
        <input type="text" v-model="operateForm.deduct" @blur="onAmountBlur(operateForm.deduct)"/>
      </el-form-item>
      <!-- 雪王币数量 -->
       <el-form-item label="宇王币数量">
        <el-input-number v-model="operateForm.coinNeed" :min="0"   />
       </el-form-item>
      <!-- 库存 -->
       <el-form-item label="库存">
        <el-input-number v-model="operateForm.balance" :min="0"   />
       </el-form-item>
  </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="confirm">
          {{operateForm.id?'修改':'添加'}}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="less" scoped>
.el-form{
  margin: 10px 0 0 10px;
}
.add{
  height: 50px;
  &:hover{
    background-color: #E9EAEC;
  }
 
  .el-button{
    margin: 10px 0 0 10px;
  }
}
.el-dialog{
  .img{
    position: relative;
    border: 1px dashed rgb(0, 0, 0);
    .el-icon{
      position: absolute;
      right: 0;
      top: 0;
    }
    span{
      display: block;
      text-align: center;
      line-height: 100px;
      font-size: 30px;
    }
    &:hover{
      cursor: pointer;
    }
    margin: 0 auto;
    width: 200px;
  height: 100px;
  img{
    height: 100%;
    width: 100%;
  }
  }
}
</style>
<script setup lang="ts">

import type { TasteCategory } from '@/types/TasteCategory';
import { type FormInstance, type FormRules } from 'element-plus';
import { ref,onMounted } from 'vue';
import TasteCategoryStore from '@/stores/TasteCategory';
const useTasteCategoryStore = TasteCategoryStore()

const goods_category=ref<TasteCategory[]>([])

const props=defineProps<{
  formData?:{
  name:string,
  categoryId:string,
  price:string,
  balance:string,
  introduction:string,
  description:string
}
}>()
// 表单数据
const form=ref({
  name:'',
  categoryId:'',
  price:'',
  balance:'',
  introduction:'',
  description:''
})
onMounted(async()=>{
  if(props.formData){
    form.value=props.formData
  }
  goods_category.value=useTasteCategoryStore.TasteCategoryList

})

//表单校验用的ref
const ruleFormRef=ref<FormInstance >()

  const rules=ref<FormRules<{name:string,price:string,balance:String,introduction:string,categoryId:string}>>({
  name:[
    {
      required: true,
      message: '请输入名称',
      trigger: 'blur'
    }
  ],
  price:[
     {
      required:true,
      pattern:/^[0-9]*$/,
      message:'请输入合法的价格',
      trigger:'blur'
     }
  ],
  balance:[
    {
      required:false,
      pattern:/^[0-9]*$/,
      message:'请输入合法的库存',
      trigger:'blur'
    }
  ],
  introduction:[
    {
      required:true,
      message: '请输入简介', 
      trigger: 'blur'
    },
    {
      required:true,
      max:20,
      message: '简介最多输入20字', 
      trigger: 'blur'
    }
],
  categoryId:[
    {
      required: true,
      message: '请选择分类',
      trigger: 'change'
    }
  ]
})


defineExpose({
  form,
  ruleFormRef,
 
  
})
</script>
<template>
<div class="form">


<el-form :model="form" label-width="80px" :rules=rules ref="ruleFormRef">
  <el-form-item label="名称" prop="name">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="分类" prop="categoryId">
      <el-select v-model="form.categoryId" placeholder="请选择">
        <el-option v-for="item in goods_category" :key="item.id" :label="item.name" :value="item.id" />
        
      </el-select>
      </el-form-item>
      <el-form-item label="库存" prop="balance">
      <el-input v-model="form.balance" />
    </el-form-item>
    <el-form-item label="价格" prop="price">
      <el-input v-model="form.price" />
    </el-form-item>
    <el-form-item label="简介" prop="introduction">
      <el-input v-model="form.introduction" />
    </el-form-item>
    <el-form-item label="描述">
      <el-input v-model="form.description" type="textarea" />
    </el-form-item>
</el-form>


   </div>
</template>
<style lang="less" scoped>
 .form{
    h5{
      margin-top: 10px;
      font-style: italic;
      color: grey;
    
    }
    padding-left: 10px;
    .pictures{
  width: 500px;
  
  padding: 10px ;
  display: flex;
  height: 200px;
  .img{
  img{
    width: 100%;
   height: 100%;
  }
    cursor: pointer;
    text-align: center;
    line-height: 150px;
    margin: 10px;
    width: 150px;
    height: 150px;
    border: 1px dashed #ccc;
    span{
      font-size: 48px;
      color: gray;
    }
  }
}

  }
</style>
<script lang="ts" setup>
import { ref,onMounted } from 'vue'
import type {TasteCategory} from '@/types/TasteCategory'
import { ElMessage } from 'element-plus'
import { getGoodsCategoryListAPI,
addCategoryAPI,
setCategoryStatusAPI,
updateCategoryAPI } from '@/apis/TasteCategory';
import { uploadImgAPI } from '@/apis/common/upload';
const categoryData=ref<TasteCategory[]>([])
const getCategoryList=async()=>{
  const res=await getGoodsCategoryListAPI()
  categoryData.value=res.data
}
onMounted(async ()=>{
  getCategoryList()
})

const drawer = ref(false)
const direction = 'rtl'
//输入框实例
const inputRef=ref()
//选中的item项
const selectedItem=ref<TasteCategory>({} as TasteCategory)

const imgChange=async(e:Event )=>{
  const target = e.target as HTMLInputElement;
  if(target.files!.length === 0){
    return console.log('未选择文件');
  }
  const file = target.files![0];
  const formData = new FormData();
  formData.append('file', file);
 const res=await uploadImgAPI(formData,'goodsCategory')
 selectedItem.value.image=res.data
}
const handlerConfirm=async()=>{
if(!selectedItem.value.name){
  return ElMessage.error('请输入分类名称')
}
if(!selectedItem.value.image){
  return ElMessage.error('请上传图片')
}
//编辑操作
if(selectedItem.value.id){
 await updateCategoryAPI(selectedItem.value)
 drawer.value=false
 getCategoryList()
}else{
  if(categoryData.value.find(v=>v.name===selectedItem.value.name)){
    return ElMessage.error('该分类名称已存在')
  }
  //新增操作
  selectedItem.value.status=1
 await addCategoryAPI(selectedItem.value)
drawer.value=false
 getCategoryList()
}
}

// 点击编辑按钮
const onEdit=(row:TasteCategory,index:number)=>{
  drawer.value=true
  selectedItem.value={...row}
}
//添加分类
const addCategory=()=>{
  drawer.value=true
 selectedItem.value={} as TasteCategory
}
//设置分类状态
const setStauts=async(row:TasteCategory,status:number)=>{
  const categoryId=row.id
 await setCategoryStatusAPI(categoryId,status)
 getCategoryList()
 
 
}
</script>
<template>
  <!-- 操作区 -->
<div class="operate">
  <el-button type="primary" @click="addCategory">添加分类</el-button>
</div>
<!-- 表格区域 -->
<el-table :data="categoryData" stripe style="width: 100%" max-height="581">
    <el-table-column prop="name" label="名称" width="280" />
    <el-table-column prop="sort" label="排序" width="280" />
    <el-table-column>
      <template #default="{row,index}">
        <el-button @click="onEdit(row,index)">编辑</el-button>
        <el-button type="danger" v-if="row.status" @click="setStauts(row,0)">禁用</el-button>
        <el-button type="primary" v-else @click="setStauts(row,1)">启用</el-button>
      </template>
    </el-table-column>
  </el-table>
<!-- 抽屉 -->
<el-drawer v-model="drawer" :direction="direction" size="620" :show-close="false">
    <template #header>
      <h4>{{selectedItem.name}}</h4>
    </template>
    <template #default>
      <div class="img" @click="inputRef.click">
        <img :src="selectedItem.image" v-if="selectedItem.image">
        <span v-else>请选择文件</span>
      </div>
      <!-- form表单 -->
      <el-form :model="selectedItem" label-width="auto" :inline="true">
      <el-form-item label="名称:"><el-input v-model="selectedItem.name" /></el-form-item>
      <el-form-item label="排序:"><el-input-number v-model="selectedItem.sort" :min="1" :value-on-clear="1"/></el-form-item>
      <el-form-item label="描述:"> <el-input v-model="selectedItem.description" style="width: 240px"  :autosize="{ minRows: 2, maxRows: 4 }" type="textarea" placeholder="请输入"
  /></el-form-item>
     </el-form> 
     
      <input type="file" hidden ref="inputRef" @change="imgChange">
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="drawer=false">取消</el-button>
        <el-button type="primary" @click=handlerConfirm>{{selectedItem.id?'确认修改':'添加'}}</el-button>
      </div>
    </template>
  </el-drawer>
</template>
<style lang="less" scoped>
.operate{
  display: flex;
  align-items: center;
  height: 60px;
  border-bottom: 1px solid black;
  
}
.img{
  line-height: 90px;
  text-align: center;
  margin: 0 auto;
  width: 480x;
  height: 90px;
  border: 1px dashed black;
  span{
   color: darkgrey;
  }
    
  &:hover{
    cursor: pointer;
  }
  img{
    display: block;
    width: 100%;
    height: 100%;
  }
}
.el-form{
  margin-top: 20px
}
</style>
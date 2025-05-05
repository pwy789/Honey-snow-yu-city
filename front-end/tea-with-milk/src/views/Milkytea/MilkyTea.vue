<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import GoodsAdd from '@/components/GoodsAdd.vue';
import {addGoodsAPI,getGoodsListAPI,setGoodsStatusAPI} from '@/apis/Goods'
import {getGoodsCategoryListAPI} from '@/apis/TasteCategory'
import type { Goods } from '@/types/Goods';
import type { TasteCategory } from '@/types/TasteCategory';
import { uploadImgAPI } from '@/apis/common/upload';
import TasteCategoryfrom from '@/stores/TasteCategory';
import {delGoodsISCheckedAPI} from '@/apis/Goods'
import { useRoute } from 'vue-router';
const route=useRoute()
const TasteCategoryStore=TasteCategoryfrom()
//控制弹层的显示与隐藏
const dialogVisible=ref(false)
//商品主图
const goodsCoverImage=ref('')
const inputRef=ref()
const router=useRouter()
//价格验证
const regex = /^(\d+)?$/;
//用户输入的跳转的页码
const pageInp = ref('')
//商品状态枚举
enum goodsStatus{
    OFF_SALE,
    ON_SALE
}
//条件类型声明
interface Condition{
  name:string,
  categoryId:string,
  priceLow:string,
  priceHigh:string,
  status:string
}
//分页条件
const pageCondition=ref({
  page:1,
  pageSize:5
})
//查询条件
const searchCondition=ref<Condition>({
  name:'' ,//名称
  categoryId:'0' ,//商品分类Id
  priceLow:'' ,//价格区间(始)
  priceHigh:'' ,//价格区间(末)
  status:'2' //状态
})
//商品分类数据
const goods_categoryList=ref<TasteCategory[]>([])
//用于存储商品数据
const GoodsList=ref<Goods[]>()
//获取商品数据
const getGoodsData=async()=>{

 const res=await getGoodsListAPI({...searchCondition.value,...pageCondition.value})
 GoodsList.value=res.data.records
 total.value=res.data.total
}

//数据量
const total=ref();
onMounted(async()=>{
  if(route.query.page){
    pageCondition.value.page=Number(route.query.page)
  }
 const categoryRes=await getGoodsCategoryListAPI()
 goods_categoryList.value=categoryRes.data
 //存储到pinia
 TasteCategoryStore.setTasteCategoryListService(categoryRes.data)
 await getGoodsData()
  //全部设为未勾选
  GoodsList.value?.forEach(v=>v.isChecked=false)
 
})
//删除已选
const delChecked=async()=>{
 const Ids= GoodsList.value!.filter(v=>{
  return v.isChecked
  }).map(v=>v.id)
  if(Ids.length===0){
    return ElMessage.warning('请选择要删除的条目!')
  }
    
  const IdsStr = Ids.map(id => `ids=${id}`).join('&');
  await delGoodsISCheckedAPI(IdsStr)
  getGoodsData()
}
//用户选择了主图
const onSelectCoverImg=async(e:Event)=>{
  const target = e.target as HTMLInputElement;
  if(target.files!.length === 0){
    return console.log('未选择文件');
  }
  const file = target.files![0];
  const formData = new FormData();
  formData.append('file', file);
  goodsCoverImage.value=(await uploadImgAPI(formData,'goods')).data
}

//编辑
const onEdit=(id:string)=>{
  router.push(`/milkytea/detail/${id}?page=${pageCondition.value.page}`)
}
//用于拿到子组件的数据
const GoodsAddRef=ref()
//添加商品
const addMilkyTea=()=>{
  //重置表单数据
  if(GoodsAddRef.value){
    const {ruleFormRef} =GoodsAddRef.value
    ruleFormRef.resetFields()
  }
goodsCoverImage.value=''
 //获取数据
 getGoodsData()
  dialogVisible.value=true
}

//确认添加
const confirmAddGoods=async()=>{
  if(!goodsCoverImage.value){
    return ElMessage.warning('请选择一张封面图!')
  }
  const {form,ruleFormRef}=GoodsAddRef.value
await ruleFormRef.validate()
 //执行到这里说明表单必须项填写完整
 const data={...form,coverImage:goodsCoverImage.value}
  await addGoodsAPI(data)
 //关闭弹层
 dialogVisible.value=false

}
const setStauts=async(id:string,goodsStatus:number)=>{
   await setGoodsStatusAPI(id,goodsStatus)
   getGoodsData()
}
//清除查询条件
const clearCondition=()=>{
  searchCondition.value={
    name:'' ,//名称
    categoryId:'0' ,//商品分类Id
    priceLow:'' ,//价格区间(始)
    priceHigh:'' ,//价格区间(末)
    status:'2' //状态
  } as Condition
  
}
//检查价格输入是否有效
const checkedPriceInp=(interval:string)=>{
  if(interval==='low'){
    if(!regex.test(searchCondition.value.priceLow)){
      searchCondition.value.priceLow=''
    return ElMessage.warning('请输入正确的最小值!')

  }
  }
   if(interval==='high'){
    if(!regex.test(searchCondition.value.priceHigh)){
      searchCondition.value.priceHigh=''
   return  ElMessage.warning('请输入正确的最大值!')

  }
  }
}
//查询
const search=()=>{
   if(searchCondition.value.priceLow>searchCondition.value.priceHigh){
    return ElMessage.warning('最小值不能大于最大值!')
   }
   pageCondition.value.page=1
   getGoodsData()
}
//上一页
const previousPage=()=>{
  if(pageCondition.value.page<=1){
    return ElMessage.warning('已经是第一页了!')
  }
  pageCondition.value.page--
 
  getGoodsData()
  
}
//下一页
const nextPage=()=>{
  if(pageCondition.value.page>=total.value/pageCondition.value.pageSize){
    return ElMessage.warning('已经是最后一页了!')
  }
  pageCondition.value.page++
  getGoodsData()
}
//页码选择
const pageSelected=(page:number)=>{
  if(page===pageCondition.value.page){
    return ElMessage.warning('已经是这一页了!')
  }
 pageCondition.value.page=page
 getGoodsData()
 
}
//前往多少页
const pageGo=()=>{
  if(!regex.test(pageInp.value)){
    pageInp.value=''
   return ElMessage.error('请输入正确的页码!')
  }
  pageCondition.value.page=Number(pageInp.value)
  getGoodsData()
  pageInp.value=''
}
</script>

<template>
  <!-- 操作台区域 -->
<div class="operate">
<ul>
  <!-- 按钮区域 -->
  <li>
    <button type="button" class="btn btn-outline-info" @click="addMilkyTea">添加商品</button> 
    &nbsp;
    <button type="button" class="btn btn-outline-danger"  data-bs-target="#Modal" data-bs-toggle="modal">删除已选</button>
  </li>
  <!-- 名称输入 -->
  <li>
      <p>名称 :&nbsp;</p>
      <input type="text" class="form-control" placeholder="Goods" v-model="searchCondition.name">
  </li>
  <!-- 奶茶类别 -->
  <li>
    <label class="input-group-text" for="inputGroupSelect01">分类</label>
  <select class="form-select" id="inputGroupSelect01" v-model="searchCondition.categoryId">
    <option value="0">全部</option>
    <option :value="item.id" v-for="item in goods_categoryList">{{ item.name }}</option>
   
  </select>
  </li>
  <!-- 价格输入 -->
   <li>
    <span>价格</span>
    <p >从 :&nbsp;</p>
    <input type="text" class="form-control" v-model="searchCondition.priceLow" @blur="checkedPriceInp('low')">
    &nbsp;
    <p >到 :&nbsp;</p>
    <input type="text" class="form-control" v-model="searchCondition.priceHigh" @blur="checkedPriceInp('high')">
   </li>
   <!-- 奶茶状态 -->
   <li >
    <label class="input-group-text" for="statusSelect">状态</label>
  <select class="form-select" id="statusSelect"  v-model="searchCondition.status">
    <option value="2">全部</option>
    <option value="1" >在售</option>
    <option value="0">停售</option>
  </select>
   </li>
   <button type="button" class="btn btn-outline-dark" @click="clearCondition">清除</button> 
   <button type="button" class="btn btn-outline-dark" @click="search">查询</button> 
</ul>
</div>
<!-- 表格区域 -->
<div class="tables">
  <table class="table">
    <thead>
    <tr >
 
      <th class="table-info">图片</th>
      <th class="table-light">名称</th>
      <th class="table-danger">类别</th>
      <th class="table-dark">价格</th>
      <th class="table-warning">库存</th>
      <th class="table-success">操作</th>
    </tr>
  </thead>
  <tbody>
    <tr v-for=" (item) in GoodsList" :key="item.id">
      
      <td class="image">
        <input class="form-check-input check" type="checkbox" v-model="item.isChecked" >
       <img :src="item.coverImage" >
      </td>
      <td><h5>{{item.name}}</h5></td>
      <td><h5>{{goods_categoryList?.find(v=>v.id===item.categoryId)?.name}}</h5></td>
      <td><h5>{{item.price}}</h5></td>
      <td><h5>{{item.balance}}</h5></td>
      <td class="btns">
        <div class="a">
          <button type="button" class="btn btn-dark" @click="onEdit(item.id)">编辑</button>
          <button type="button" class="btn btn-info" v-if="!item.status" @click="setStauts(item.id,goodsStatus.ON_SALE)">启售</button>
          <button type="button" class="btn btn-warning" v-else @click="setStauts(item.id,goodsStatus.OFF_SALE)">禁售</button>
        </div>
      
      </td>
    </tr>

  </tbody>
  </table>
</div>
<!-- 弹框Model显示 -->
<div class="modal fade" id="Modal" tabindex="-1"  aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="exampleModalLabel">温馨提示</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <h4>您确认要删除吗?</h4>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" data-bs-dismiss="modal"  @click="delChecked()">确定</button>
      </div>
    </div>
  </div>
</div>
<!-- 添加商品dialog -->
<el-dialog
    v-model="dialogVisible"
    title="添加商品"
    width="30%"
  >
  <div class="img" @click="inputRef.click()">
    <img :src="goodsCoverImage" alt="" v-if="goodsCoverImage">
    <span v-else>+</span>
  </div>
  <input type="file" hidden ref="inputRef" @change="onSelectCoverImg">
  <GoodsAdd ref="GoodsAddRef"></GoodsAdd>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddGoods">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
<!-- 分页 -->
<nav aria-label="Page navigation example" class="Pagination">
  <ul class="pagination">
    <li class="page-item" style="margin-top: 6px; margin-right: 20px;">
      当前页: {{ pageCondition.page }}
    </li>
    <!-- 上一页 -->
    <li class="page-item" @click="previousPage">
      <a class="page-link" href="#" aria-label="Previous">
        <span aria-hidden="true">&laquo;</span>
      </a>
    </li>
    <!-- 页码 -->
    <li class="page-item" v-for="(item) in Math.ceil(total/pageCondition.pageSize) "
     :key="item" @click="pageSelected(item)" v-if="total"><a class="page-link" href="#">{{item}}</a></li>
  <!-- 下一页 -->
    <li class="page-item" @click="nextPage">
      <a class="page-link" href="#" aria-label="Next">
        <span aria-hidden="true">&raquo;</span>
      </a>
    </li>
    <!-- 页面跳转 -->
    <li class="page-go">
      <input type="text" class="form-control pageInp" v-model="pageInp">
      <div class="go" @click="pageGo">go</div>
    </li>
  </ul>
</nav>
</template>

<style lang="less" scoped>
.operate{
  margin: 20px 0;
  padding: 10px;
  border-bottom: 1px solid rgba(0,0,0,.125);
  box-shadow: 0 10px 5px rgba(0,0,0,.1);
  ul{
    display: flex;
    justify-content: space-between;
    height: 40px;
    li{
      display: flex;
      align-items: center;
    }
  }
  input{
    width: 150px;
  }
  label{
    display: inline;
  }
}
.tables{

 .table{
  //固定列的宽度,让其宽度不因为内容的多少而显示宽度变化
  table-layout: fixed;
  
  .image{
   
    position: relative;
    img{
      margin: 0 auto;
    width: 45%;
    height: 80px;
   
  }
  .check{
     position: absolute;
     top: 40%;
}
  }
  td{
    
    h5{
      margin-top: 25px;
      text-align: center;
    }
  
  }
  .a{
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: space-around;
  }
 
 }
}

  .Pagination{
    display: flex;
    justify-content: center;

    position: absolute;
    left: 45%;
    bottom: 10%;
  
  .page-go{
    margin-left: 40px;
    display: flex;
    .pageInp{
    width: 60px;
  }
  .go{
    padding-top: 3px;
    margin-left: 10px;
    cursor: pointer;
    text-align: center;
    color: #DEE2E6;
    width: 40px;
    border: 1px #DEE2E6 solid;
    border-radius: 5px;
    &:hover{
      background-color: #F8F9FA;
      color: #0A58CA;
    }
  }
  }
 
}
.img{
  img{
    width: 100%;
   height: 100%;
  }
    cursor: pointer;
    text-align: center;
    line-height: 150px;
    margin: 10px auto;
    width: 150px;
    height: 150px;
    border: 1px dashed #ccc;
}


</style>
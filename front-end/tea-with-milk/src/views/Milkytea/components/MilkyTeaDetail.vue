<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import {ref, watch,onMounted} from 'vue'
import type { SkuCategory,sku } from '@/types/SkuCategory';
import type {TasteCategory} from '@/types/TasteCategory'
import {getDimensionSkusAPI} from '@/apis/SkuCategory'
import type {GoodsInfo, GoodsPictures} from '@/types/Goods'
import { uploadImgAPI } from '@/apis/common/upload';
import { updateGoodsAPI,getGoodsByIdAPI } from '@/apis/Goods';
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
import TasteCategoryStore from '@/stores/TasteCategory';
import GoodsAdd from '@/components/GoodsAdd.vue';

const route=useRoute()
const router=useRouter()
const useTasteCategoryStore=TasteCategoryStore()
//拿到子组件form表单数据
const GoodsAddRef=ref()
//拿到当前的页码,以便返回时回到最初的页码
const currPage=route.query.page
//用于存储Sku价格输入
const skuPriceInp=ref(1)

//用于让后端区分是要执行插入,更新,删除中的哪一个
const imageOperate=ref<{id?:number, url: string; operation: 'add' | 'update' | 'delete'}[]>([])
//同理图片,给后端区分是新增操作还是删除操作
const skuOperate = ref<{ skuId: number; operate: 'add' | 'delete'|'update',price?:number }[]>([]);
//用于接收后端传来的完整的商品数据
const goods=ref<GoodsInfo>()

const goodsId=route.params.id as string
//添加商品所需要的信息
const goodsInfo=ref<GoodsInfo>()

//该商品选中的主类及sku
const mainAndSkuData=ref<{id:number,skus:{id:number,price?:number}[]}[]>([])
//所有状态为启用的商品分类
const goods_category=ref<TasteCategory[]>([])
//所有的主类及其sku
const options=ref<SkuCategory[]>([])
//商品的图片信息
const goodsImages=ref<GoodsPictures[]>([])
//用于记录用户点击的是第几张图片
const imgIndex=ref(0)
//dialog显示与隐藏
const dialogVisible=ref(false)
//获取商品分类、sku等信息
onMounted(async()=>{

const dimensionSkusRes=await getDimensionSkusAPI()
goods_category.value=useTasteCategoryStore.TasteCategoryList
 options.value=dimensionSkusRes.data
const res=await getGoodsByIdAPI(goodsId)
goods.value=res.data
//图片赋值
goodsImages.value.push(...goods.value!.images.map(v=>{
  return {
    id:v.id,
    url:v.url,
    goodsId
  }
}))
//表单赋值
forms.value={
  name:goods.value!.name,
  categoryId:goods.value!.categoryId,
  price:goods.value!.price,
  balance:goods.value!.balance,
  introduction:goods.value!.introduction,
  description:goods.value!.description,
}
 //sku赋值
 const a=res.data.skus as {goodsId:number,skuId:number,price?:number}[]
const skuIds=a.map(v=>v.skuId)
 //i:每一个主类 v:每一个主类下的sku  
 options.value.forEach(i=>{
  i.skus.forEach(v=>{
    //说明该sku被选中了且主类还没有被创建
    if(skuIds.includes(v.id)&&!mainAndSkuData.value.find(j=>j.id===i.id)){
      mainAndSkuData.value.push({
        id:i.id,
        skus:[{
          id:v.id,
          price:a.find(p=>p.skuId===v.id)?.price
        }]
      })
      //说明没有被选中,直接到下一次循环
    }else if(!skuIds.includes(v.id)){
          return
    }
    //说明该sku被选中了且主类已经创建
    else{
      mainAndSkuData.value.find(x=>x.id===i.id)?.skus.push({
      id:v.id,
      price:a.find(p=>p.skuId===v.id)?.price
    })
    }
  })
 }) 

})
//用户选中某一个主类后获取该主类下的所有sku
const optionsItem=ref<sku[]>([])
//用户选中的主类
const selectedMain=ref()
watch(selectedMain,(newValue)=>{
  optionsItem.value=options.value.find(v=>v.id===newValue)!.skus
const rs=mainAndSkuData.value.find(v=>v.id===newValue)
if(rs){
  selectedSkus.value=rs.skus.map(v=>v.id)
}else{
  selectedSkus.value=[]

}
})

//选中的sku
const selectedSkus=ref<number[]>([])


// 表单数据
const forms=ref({
  name:'',
  categoryId:'',
  price:'',
  balance:'',
  introduction:'',
  description:''
})
//input文件上传Ref
const imgRef = ref<HTMLInputElement>()
const handlerChangeImg=(index?:number)=>{
  // 如果index没有传,说明用户在添加图片,如有index有值,这个index是图片的索引
  if(index!=null){
    console.log(`用户点击的是第${index+1}张图片`);
    
    imgIndex.value=index
  }else{
    //用户在添加图片,让imageIndex指针指向下一张图片
 imgIndex.value=goodsImages.value.length
  }
 
  imgRef.value!.click()
  
}
//用户选择了图片后触发的回调
const onImgSelected=async(e:Event)=>{
    const target=e.target as HTMLInputElement;
   if(!target.files![0]){
    return ElMessage.error('未选择文件')
   }
  
   // 确保 goodsImages.value 中有足够多的元素
  if (imgIndex.value >= goodsImages.value.length) {
    goodsImages.value.push({
      url: '', // 初始化为空字符串
      goodsId: goodsId,
    });
  } 
    const formData=new FormData()
    formData.append('file',target.files![0])
   const res=await uploadImgAPI(formData,'goods')
   
   // 执行插入操作
   if(!goodsImages.value[imgIndex.value].id){
    if(goodsImages.value[imgIndex.value].url){
       imageOperate.value.find(v=>v.url===goodsImages.value[imgIndex.value].url)!.url=res.data 
    }else{
      imageOperate.value.push({
      url: res.data,
      operation: 'add'
    });
    }
   
   }else{
    //有id,说明是在原有图片上进行修改
    if(imageOperate.value.find(v=>v.id===goodsImages.value[imgIndex.value].id)?.id){
      imageOperate.value.find(v=>v.id===goodsImages.value[imgIndex.value].id)!.url=res.data
    }else{
      imageOperate.value.push({
      id: goodsImages.value[imgIndex.value].id,
      url: res.data,
      operation: 'update'
    });
    }
   
   }
   goodsImages.value[imgIndex.value].url=res.data
}
//删除选中的图片
const delImg=async(index:number)=>{
   const imageId=goodsImages.value[index].id
   const url= goodsImages.value[index].url
   const isExist= imageOperate.value.findIndex(v=>v.url===url)
    if(isExist!==-1){
      imageOperate.value.splice(isExist,1)
    }
   
    //有id,说明是数据库中已有的图片,需要删除
   if(imageId){
  
      //没有记录,push
      imageOperate.value.push({
      id:goodsImages.value[index].id,
      url:goodsImages.value[index].url,
      operation:'delete'

    })
    
  
  }
  goodsImages.value.splice(index,1)
 
}
//保存
const save=async()=>{
  const {form,ruleFormRef}=GoodsAddRef.value
  if(goodsImages.value.length<1){
    return ElMessage.warning('请至少上传一张图片,因为它将作为封面图')
  }
  try{
   await ruleFormRef.validate()
  }catch(err:any){
   return ElMessage.error('请填写完整信息!')
  }
  //执行到这里说明必要信息填写完整
  //对于商品-维度-sku方面,只需要返回skuId即可,
  const skuPre=goods.value!.skus?.map(v=>v.skuId) //这是修改前的数组
   const skuIds=mainAndSkuData.value.map(v=>v.skus.map(v=>v.id)).flat() //这是修改后的ids
     // 判断新增和删除的 SKU
  skuIds.forEach(skuId => {
    const mainItem = mainAndSkuData.value.find(main => main.skus.some(s => s.id === skuId));
  if (mainItem) {
    const skuPrice = mainItem.skus.find(s => s.id === skuId)!.price;
    
    if (!skuPre?.includes(skuId)) {
      skuOperate.value.push({ skuId, operate: 'add', price: skuPrice });
    } else {
     //看看价格是否修改,如果修改了,进pushupdate
       if(goods.value?.skus?.find(v=>v.id===skuId)?.price!==skuPrice){
        skuOperate.value.push({ skuId, operate: 'update', price: skuPrice });
       } 
    }
  }
});

  skuPre!.forEach(sku => {
    if (!skuIds.includes(sku)) {
      skuOperate.value.push({
        skuId: sku,
        operate: 'delete',
       
      });
    }
  });
   goodsInfo.value={
    name:form.name,
    categoryId:form.categoryId,
    price:form.price,
    balance:form.balance,
    introduction:form.introduction,
    description:form.description,
    status:0,
    id:goodsId,
    images:[goodsImages.value[0]],
    skuOperate:skuOperate.value
    
   }
  await updateGoodsAPI({...goodsInfo.value,imageOperate:imageOperate.value})
  setTimeout(() => {
    router.replace(`/milkytea?page=${currPage}`)
  }, 1500);
  
}
const skuChange=(val:number[])=>{
//表示该主类下的sku全删完了
if(val.length===0){
   //从选中的主类中去除该主类
 return mainAndSkuData.value= mainAndSkuData.value.filter(v=>v.id!==selectedMain.value)
}
//表示从没有的主类中添加了一个sku
if(!mainAndSkuData.value.find(v=>v.id===selectedMain.value)&&val.length===1){
 return mainAndSkuData.value.push({
    id:selectedMain.value,
    skus:[
      {
      ...options.value.find(v=>v.id===selectedMain.value)!.skus.find(v=>v.id===val[0])!
    }
  
    ]
  })
}
//常规的添加sku
const subs=options.value.find(i=>i.id===selectedMain.value)!.skus.filter(v=>val.includes(v.id))
mainAndSkuData.value.find(i=>i.id===selectedMain.value)!.skus=subs

}
//用户点击的那个sku对应的主类Id
const selectedMainId=ref()
//用户当前选中的sku的Id
const selectedSkuId=ref()
const onSetPrice=(mainId:number,skuId:number)=>{
 
  dialogVisible.value=true
  selectedMainId.value=mainId
  selectedSkuId.value=skuId
  const price=mainAndSkuData.value.find(v=>v.id===selectedMainId.value)?.skus.find(v=>v.id===skuId)?.price
  if(price){
    skuPriceInp.value=price
  }else{
    skuPriceInp.value=1

  }
  
}
//确认修改sku的价格
const confirmPrice=()=>{
  //输入的价格
const price=skuPriceInp.value
  dialogVisible.value=false
  //更新数据
  mainAndSkuData.value.find(v=>v.id===selectedMainId.value)!.skus.find(v=>v.id===selectedSkuId.value)!.price=price
  
}
</script>
<template>

 
   <div class="detail">
   <div class="left">
   <div class="form">
    <!-- 图片区域 -->
    <h5>注:一种奶茶最多保存三张图片!</h5>
    <div class="pictures">
  <div class="img" @click="handlerChangeImg(index)" v-for="(item,index) in goodsImages" v-if="goodsImages.length" :key="index">
    <img :src="item.url" v-if="item.url">
    <span v-else>+</span>
    <el-icon size="20" @click.stop="delImg(index)" v-if="goodsImages.length>1"><CircleCloseFilled /></el-icon>
  </div>
  <!-- 当goodsImages等于3时,这个盒子就该消失了 -->
  <div class="img" @click="handlerChangeImg()" v-if="goodsImages.length<3">
    <span>+</span>
  </div>
  <input type="file" hidden ref="imgRef" @change="onImgSelected">

</div>

<GoodsAdd ref="GoodsAddRef" :formData="forms" v-if="goods?.id"></GoodsAdd>
<div class="btns">
  <el-button type="primary" @click="$router.push(`/milkytea?page=${currPage}`)">返回</el-button>
  <el-button color="#FEB102" @click="save">保存</el-button>
</div>

   </div>
  
   </div>

  <!-- sku区域 -->
   <div class="right">
    <div class="content">
    <div class="item" v-for="item in mainAndSkuData" :key="item.id">
      <span class="title">{{ options.find(v=>v.id===item.id)?.name }}</span>
      <ul>
      <li v-for="s in item.skus" :key="s.id" @dblclick="onSetPrice(item.id,s.id)">
          {{ options.find(v=>v.id===item.id)?.skus.find(a=>a.id===s.id)?.name }}{{ s.price?`|￥`+s.price:'' }}
        </li>
      </ul>
    </div>
    </div>
    <!-- 控制台区域 -->
   
    <div class="console">
      <h4>控制台</h4>
      <div class="operate">
        <div class="main">
        <span>主类:</span>  
      <el-select
    v-model="selectedMain"
    class="m-2"
    placeholder="请选择"
    size="large"
    style="width: 240px"
  >
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="item.name"
      :value="item.id"
    />
    </el-select>
      </div>
   <div class="sku">
    <span>SKUS:</span>
    <el-select
      v-model="selectedSkus"
      multiple
      placeholder="Select"
      style="width: 240px"
      @change="skuChange"
    >
      <el-option
        v-for="item in optionsItem"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
   </div>
      </div>
    </div>
   </div>
   </div>
<!-- dialog显示与隐藏 -->
 <el-dialog v-model="dialogVisible" title="价格修改" width="20%"> 
 <el-input-number :min="1" v-model="skuPriceInp"/> &nbsp;

 <template #footer>
  <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPrice">
         确认
        </el-button>
 </template>
 </el-dialog>

</template>
<style lang="less" scoped>
.detail{
  height: 600px;
  display: flex;
  .left{
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
  width: 150px;
  img{
    
    width: 100%;
   height: 100%;
  }
  position: relative;
  .el-icon{
    position: absolute;
    top: 0;
    right: 0;
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
.btns{
 display: flex;
 justify-content: center;
}
  }
   
  }
  .right{
    padding: 10px;
    flex:1;
   
    .content {
      max-height: 80%;
      overflow: scroll;
      background-color: #f9f9f9; /* 设置背景颜色 */
      border: 1px solid #e0e0e0; /* 添加边框 */
      border-radius: 8px; /* 圆角 */
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08); /* 轻微阴影 */
      padding: 20px; /* 增加内边距 */
      margin-bottom: 20px; /* 增加底部外边距 */
     .item {
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: rgba(240, 240, 240, 0.8);
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        padding: 15px;
        margin-bottom: 15px;
       .title {
          padding: 5px;
          margin: 5px 0;
          background-color: antiquewhite;
          display: inline-block;
          border-radius: 4px;
        }
        ul {
          display: flex;
          flex-wrap: wrap;
          li {
          cursor: pointer;
            color: white;
            margin: 0 10px;
            padding: 5px;
            background-color: #2f80c2;
            border-radius: 4px;
          }
        }
      }
    }
  .console{
    border-top: 1px solid #ba9494;
    padding: 10px;
    height: 20%;
    .operate{
      display: flex;
    justify-content: space-around;
    align-items: center;
    }
    h4{
      font-style: italic;
    }
  }
  }
}

.el-form{
  margin-top: 45px;
  width: 400px;
}

</style>
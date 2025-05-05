<script lang="ts" setup>
import { ref,onMounted } from 'vue';
import { Check, Close,Delete } from '@element-plus/icons-vue';
import {getDimensionSkusAPI,
    delSkuByIdAPI,addMainAPI,
    addSkuAPI, updateMainAPI
    ,updateSkuAPI,delMainAPI} from '@/apis/SkuCategory'
import type { SkuCategory } from '@/types/SkuCategory';
import { ElMessage, ElMessageBox } from 'element-plus';
//选中的主类索引
const mainIndex=ref(0)
//所有主类及其sku
const options=ref<SkuCategory[]>([])
//控制dialog的显示与隐藏
const dialogTableVisible=ref(false)
//控制dialog是显示主类的内容还是sku
const dialogShowWhat=ref<mainOrSku>()
enum mainOrSku{
    main,
    sku
}
//dialog中显示的数据
const dialogData=ref<{id:number,name:string,sort?:number}>()
// 主类被选择时触发
const onMainSelected=(id:number)=>{
mainIndex.value=id
}
//获取所有主类及其sku
const getData=async()=>{
    const res=await getDimensionSkusAPI()
    options.value=res.data
}
onMounted(async()=>{ 
   await getData()
    mainIndex.value=options.value[0].id
})
//点击X,删除某个sku
const onDelSku=async(id:number)=>{
   await delSkuByIdAPI(id)
   getData()
}
//用户输入的主类名
const mainInp=ref('')
//用户输入的sku名
const skuInp=ref('')

//用户输入的主类sort
const DimensionSortInp=ref(999)

//添加主类
const addMain=async()=>{
    const trimMainInp=mainInp.value.trim()
    if(!trimMainInp){
      return  ElMessage.warning('请输入主类名')
    }
    if(options.value.find(v=>v.name===trimMainInp)){
        mainInp.value=''
        return ElMessage.warning('该主类已存在')
    }
   await addMainAPI({name:trimMainInp,sort:DimensionSortInp.value})
    mainInp.value=''
   getData()
}
//添加Sku
const addSku=async()=>{
    //去除空格
    const trimSkuInp=skuInp.value.trim()
    if(!trimSkuInp){
        return  ElMessage.warning('请输入sku名')
    }
    const dimensionId=mainIndex.value
    if(options.value.find(v=>v.id===mainIndex.value)?.skus.find(v=>v.name===trimSkuInp)){
        return ElMessage.warning('该sku已存在')
    }
    
       await addSkuAPI({name:trimSkuInp,dimensionId,})
  
    skuInp.value=''
    
    getData()
}
//双击修改选中的主类或者sku
const updateShowDialog=(e:mainOrSku,id:number)=>{
   dialogTableVisible.value=true
   dialogShowWhat.value=e
   //主类
   if(e===mainOrSku.main){
    const main=options.value.find(v=>v.id===id)
    dialogData.value={id:main!.id,name:main!.name,sort:main!.sort}
    //sku
   }else{
    const sku=options.value.find(v=>v.id===mainIndex.value)?.skus.find(v=>v.id===id)

        dialogData.value={id:sku!.id,name:sku!.name}
    
   }
    
}
//确认修改主类或者sku
const confirmChange=async()=>{
    //主类
    if(dialogShowWhat.value===mainOrSku.main){
        const trimMainInp=dialogData.value!.name.trim()
       if(!trimMainInp){
        return ElMessage.warning('主类名不能为空')
       }
      
       await updateMainAPI({id:dialogData.value!.id,name:trimMainInp,sort:dialogData.value!.sort!})
       //sku
    }else{
       const trimSkuInp=dialogData.value!.name.trim()
       if(!trimSkuInp){
        return ElMessage.warning('sku名不能为空')
       }
       
          await updateSkuAPI({id:dialogData.value!.id,name:trimSkuInp})
       
    }
    getData()
    dialogTableVisible.value=false
}
//删除主类
const  delMain=()=>{
    ElMessageBox.confirm(
      '您确定要删除该主类吗?',
      '温馨提示(包含该主类的所有sku都将被删除!)',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(async()=>{
      await delMainAPI(mainIndex.value)
      getData()
    }).catch(()=>{})
}
</script>
<template>
  <div class="skuCategory">
    <!-- 左侧主类 -->
   <div class="main"> 
       <ul>
        <!-- 每一个li为一个主类 -->
        <li class="mainitem" v-for="(item) in options" :key="item.id" 
        :class="{ active: mainIndex===item.id }" 
        @click="onMainSelected(item.id)" @dblclick="updateShowDialog(mainOrSku.main,item.id)">
          <h5>
            {{item.name}}
          </h5>
        </li>
       </ul>
   </div>
   <!-- 右侧sku -->
   <div class="subs">
     <ul v-if="options.find(v=>v.id===mainIndex)?.skus.length">
      <li v-for="item in options.find(v=>v.id===mainIndex)?.skus" 
        :key="item.id" @dblclick="updateShowDialog(mainOrSku.sku,item.id)">
        <h5>{{ item.name}}</h5>
        <span @click="onDelSku(item.id)">x</span></li>
     </ul>
      <el-empty description="没有数据..." v-else class="empty"/> 
     <!-- 控制台区域 -->
      <div class="console">
        <!-- sku控制台部分 -->
        <div class="skuconsole">
          <h4>SKU控制台</h4>
       <label for="skunameInp">sku名称 :</label>
       <el-input placeholder="请输入sku" id="skunameInp" v-model="skuInp"/>
       <br> 
       <el-button  color="#626aef" class="insert" @click="addSku">添加</el-button>
       
        </div>
         <!-- 主类控制台部分 -->
       <div class="mainconsole">
        <h4>主类控制台</h4>
        <h5>添加主类:</h5>
        <el-input placeholder="请输入主类名" v-model="mainInp"></el-input> <el-button type="primary" @click="addMain">添加</el-button>
        <h5>设置排序(请务必将商品的规格排序设为最小值!)</h5>
        <el-input-number :min="1" v-model="DimensionSortInp" :max="999" :value-on-clear="999"></el-input-number>
        <h5>删除该主类:</h5>
        <el-button type="danger" circle :icon="Delete" @click="delMain"></el-button>
       </div>
      </div>
     
   </div>
   </div>
   <!-- 弹层 -->
   <el-dialog v-model="dialogTableVisible" title="修改" width="30%">
  <span>名称:</span> <el-input v-model="dialogData!.name"></el-input>
  <br><br>
  <span v-if="dialogShowWhat===mainOrSku.main">排序:</span>
  <el-input-number :min="1" v-model="dialogData!.sort" :max="999" :value-on-clear="999" v-if="dialogShowWhat===mainOrSku.main"/>
  <template #footer>
    <el-button @click="dialogTableVisible=false">取消</el-button>
    <el-button @click="confirmChange">确认修改</el-button>
  </template>
  </el-dialog>
</template>
<style scoped lang="less">
 .skuCategory{
    padding: 10px;
   display: flex;
    height: 600px;
   
    .main {
        width: 150px;
        height: 100%;
        background-color: #f5f5f5; /* 调整背景颜色 */
        border-radius: 10px;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);

        ul {
            overflow: auto;
            height: 100%;
            scrollbar-width: thin;
            scrollbar-color: #ccc #f5f5f5;

            &::-webkit-scrollbar {
                width: 6px;
            }

            &::-webkit-scrollbar-thumb {
                background-color: #ccc;
                border-radius: 3px;
            }

            &::-webkit-scrollbar-track {
                background-color: #f5f5f5;
            }
        }

        .mainitem {
            margin: 10px 10px;
            text-align: center;
            cursor: pointer;
            border-radius: 10px;
            height: 50px;
            border: none;
            background: linear-gradient(to bottom, #ffffff, #f0f0f0);
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;

            h5 {
                line-height: 50px;
                font-weight: 600;
                color: #333;
                text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8);
            }

            &:hover {
                background: linear-gradient(to bottom, #EEB78C, #e5a373);
                color: #FFF4EC;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }

            &.active {
                background: linear-gradient(to bottom, #EEB78C, #e5a373);
                color: #FFF4EC;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                transform: translateY(-2px);
            }
        }
    }
    .subs {
        flex: 1;

        ul {
            width: 100%;
            height: 50%;
            display: flex;
            flex-wrap: wrap;
            overflow: scroll;
            box-shadow: 5px 5px 5px rgba(0,0,0,0.5);

            li {
                cursor: pointer;
                color: #333;
                position: relative;
                padding: 15px;
                background: linear-gradient(to bottom, #ffffff, #f8f8f8);
                margin: 10px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                height: 50px;
                h5 {
                    margin: 0 5px;
                    font-size: 16px;
                    font-weight: 500;
                }

                span {
                    cursor: pointer;
                    position: absolute;
                    right: 0;
                    top: 50%;
                    transform: translateY(-50%);
                    font-size: 20px;
                    color: #999;
                    transition: color 0.3s ease;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    width: 20px;
                    height: 20px;
                    border-radius: 50%;
                    margin-left: 10px;
                   

                    &:hover {
                        color: #333;
                        background-color: #eee;
                    }
                }

                &:hover {
                    background: linear-gradient(to bottom, #f8f8f8, #eeeeee);
                    transform: translateY(-2px);
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                }
            }
        }
          .empty{
            height: 50%;
            box-shadow: 5px 5px 5px rgba(0,0,0,0.5);
          }
        .console {
            display: flex;
            width: 100%;
            padding: 10px;
            height: 50%;

            .skuconsole {
                width: 50%;
                border-right: 5px double rgba(0,0,0,0.5);
                height: 100%;
            }

            .mainconsole {
                padding: 10px;
                flex: 1;
            }

            h4 {
                font-style: italic;
            }

            .el-input {
                width: 300px;
            }
        }
    }
  }
  .el-dialog{
    .el-input{
        width: 250px;
    }
  }
</style>
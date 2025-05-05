<script lang="ts" setup>
import { useEmployeeStore } from '@/stores/Employee';
import { ref,onMounted } from 'vue';
import {getOrdersByIdAPI,getOrdersListAPI,searchOrdersAPI,finishOrderAPI} from '@/apis/Order'
import type { orders,PageOrders } from '@/types/Orders';
import { ElMessage, type TabsPaneContext } from 'element-plus'
import dayjs  from 'dayjs';
const employeeStore=useEmployeeStore()

const ordersList=ref<orders[]>()
//控制抽屉的显示与隐藏
const showDrawer=ref(false)
//获取待制作的订单数据
const getPendingOrders=async()=>{
  const res=await getOrdersListAPI(2)
  ordersList.value=res.data
}
//获取订单数据
onMounted(async()=>{
 await getPendingOrders()
 connectWebsocket()
 search()
})
//选项栏索引
const tabIndex=ref('0')
//建立websocket连接
 const connectWebsocket=()=>{

  
  //创建websocket实例
  let webSocket=new WebSocket('ws://localhost:8080/orderSocket')
 
 //连接建立成功时触发
 webSocket.onopen=()=>{
  //将店铺的id传给后端
 const shopId= employeeStore.employeeInfo!.shopId
 webSocket.send(shopId)
 }
 //收到消息时触发
 webSocket.onmessage=async(msg)=>{
 
    ElMessage.success('新订单来了')
  //这个id是订单表的主键id
  const id=JSON.parse(msg.data).id
  if(ordersList.value?.find(item=>item.id===id)) return
  //查询订单
 const res=await getOrdersByIdAPI(id)
 ordersList.value!.push(res.data)
 
 

  
 }
 //连接关闭时触发
 webSocket.onclose=()=>{
 }
}

const activeName=ref('first')
const handleClick=(tab: TabsPaneContext)=>{
  //选项栏索引
tabIndex.value=tab.index!
}
const form=ref({
  
  orderNum:'',
  beginTime:'',
  endTime:'',
  status:'',
  page:1,
  pageSize:5
})
//状态选择
const statusOptions=[
  { label: '待支付', value: '0' },
  { label: '已取消', value: '1' },
  { label: '制作中', value: '2' },
  { label: '配送中', value: '3' },
  { label: '已完成', value: '4' },
  { label: '已送达', value: '5' }
]
//清空form
const clear=()=>{
 form.value={ 
  orderNum:'',
  beginTime:'',
  endTime:'',
  status:'',
  page:1,
  pageSize:10
}
}
//存放抽屉里的内容
const drawerContent=ref<orders>()
//获取详情
const getDetail=(row:orders)=>{

  showDrawer.value=true
  drawerContent.value=row
  
  
}
//数据总数
const total=ref(0)
const tableData=ref<PageOrders>({} as PageOrders)
//搜索
const search=async()=>{
  const newForm = { ...form.value };
  if (newForm.beginTime) {
    newForm.beginTime = dayjs(newForm.beginTime).format('YYYY-MM-DD HH:mm:ss');
  }
  if (newForm.endTime) {
    newForm.endTime = dayjs(newForm.endTime).format('YYYY-MM-DD HH:mm:ss');
  }
 const res=await searchOrdersAPI(newForm)
 tableData.value=res.data
 tableData.value.ov.forEach(item=>{
  if(item.createTime)  item.orderTime=dayjs(item.orderTime).format('YYYY-MM-DD HH:mm:ss')
  item.createTime=dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss')
  item.status=statusOptions.find(f=>f.value===item.status.toString())!.label
 })
 total.value= tableData.value.total
  
}
//页码变化时触发
const onPageChange=(p:number)=>{
form.value.page=p
search()

}
//每一页大小变化时触发
const onPageSizeChange=(size:number)=>{
 form.value.pageSize=size
 search()
}
//完成订单
const finishOrder=async(id:string)=>{
 await finishOrderAPI(id)
 getPendingOrders()
}
</script>
<template>
  <!-- 标签选择 -->
  <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick" :style="{paddingLeft: '20px'}">
    <el-tab-pane label="待制作" name="first"></el-tab-pane>
    <el-tab-pane label="全部订单" name="second"></el-tab-pane>
  </el-tabs>
  <!-- 待制作内容部分 -->
  <ul class="infinite-list" style="overflow: auto" v-if="tabIndex==='0'" >
    <li v-for="i in ordersList" :key="i.id" v-if="ordersList&&ordersList.length>0">
      <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>订单号：{{ i.orderId }}</span>
        <span v-if="i.deliveryFee">配送费￥{{ i.deliveryFee }}</span>
        <span v-if="i.packagingFee">打包费￥{{ i.packagingFee }}</span>
        <span v-if="i.voucherName">优惠券：{{ i.voucherName }}</span>
        <span v-if="i.address">配送地址：{{ i.address }}</span>
        <el-popover placement="top-start" title="remark" :width="200" trigger="hover" :content="i.remark">
          <template #reference>
            <span>备注</span>
          </template>
         
        </el-popover>
  
        <el-button class="button" text @click="finishOrder(i.id)">{{ i.deliveryFee?'制作完成并联系外送员':'制作完成' }}</el-button>
      </div>
    </template>
    <div v-for="g in i.ogv" :key="g.coverImage" class="item">
      <img :src="g.coverImage">
      <p>{{ g.goodsName }}</p>
      <p>{{ g.skuInfo }}</p>
      <p class="price">￥{{ g.price }}</p>
      <p class="num">x{{ g.count }}</p>
      
    </div>
    <template #footer >
      <div class="card-footer">
      <p>下单时间：{{i.orderTime.substring(0,i.orderTime.indexOf('.')).replace('T',' ')}}</p> 
      <p class="price">总价：￥{{ i.totalPrice }}</p> 
      <p>联系人：{{ i.name }}</p>
      <p>联系电话：{{ i.phone }}</p>
      </div>
   
    </template>
  </el-card>
    </li>
    <el-empty description="暂无订单" v-else/>
  </ul>
  <!-- 全部订单内容部分 -->
   <div class="all" v-else>
      <!-- 表单部分 -->
       <div class="form">
        <el-form :inline="true">
      <el-form-item label="订单号">
        <el-input v-model="form.orderNum" />
      </el-form-item>
      <el-form-item>
        <el-date-picker
        v-model="form.beginTime"
        type="datetime"
        placeholder="起始日期"
        clearable
      />
      </el-form-item>
      <el-form-item>
        <el-date-picker
        v-model="form.endTime"
        type="datetime"
        placeholder="结束日期"
        clearable
      />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="form.status"  placeholder="请选择" style="width: 200px" clearable >
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      </el-form-item>
      <el-form-item>
        <el-button color="#545C64" @click="search">搜索</el-button>
      </el-form-item>
      <el-form-item>
        <el-button color="#B1B5F7" @click="clear">清空</el-button>
      </el-form-item>
    </el-form>
       </div>
     <!-- 表格区域 -->
        <el-table :data="tableData.ov" stripe style="width: 100%" height="550">
    <el-table-column prop="orderId" label="订单号" width="180" />
    <el-table-column prop="status" label="订单状态" width="180" />
    <el-table-column prop="createTime" label="创建时间" width="200"/>
    <el-table-column  label="操作">
     <template #default="scope">
      <el-button color="#1F1F1F" @click="getDetail(scope.row)">详情</el-button>
     </template>
    </el-table-column>
  </el-table>
      </div>
  
 <!-- 抽屉 -->
 <el-drawer
    v-model="showDrawer"
    title="商品详情"
    direction="rtl"
    size="800"
    
  >
  <template #header>
    <h3>商品详情</h3>
    <h4>{{ drawerContent?.status }}</h4>
  </template>
  <template #default>
      <h6>购买方式：{{ drawerContent?.deliveryFee?'外送':'自提' }}</h6>
      <h6>联系人：{{ drawerContent?.name }}</h6>
      <h6>性别：{{ drawerContent?.gender?'女':'男' }}</h6>
      <h6>联系电话：{{ drawerContent?.phone }}</h6>

      <!-- 商品区域 -->
      <div class="goods">
        <ul>
          <li class="drawer-item" v-for="item in drawerContent?.ogv" :key="item.coverImage">
           <img :src="item.coverImage" class="img"/>
           <p>{{ item.goodsName }}</p>
           <p>{{ item.skuInfo }}</p>
           <p>￥{{ item.price }}</p>
           <p>x{{ item.count }}</p>
          </li>
        </ul>
      </div>
        <!-- 备注 -->
        <p v-if="drawerContent?.remark">备注：{{ drawerContent?.remark }}</p>
     </template>
    <template #footer>
      <h5 v-if="drawerContent?.orderId">订单号：{{drawerContent?.orderId}}</h5>
      <h5 v-if="drawerContent?.deliveryFee">配送费：￥{{ drawerContent?.deliveryFee }}</h5>
      <h5 v-if="drawerContent?.packagingFee">打包费：￥{{ drawerContent.packagingFee }}</h5>
     <h5 v-if="drawerContent?.address">收货地址：{{ drawerContent?.address }}</h5>
     <h5 >优惠券：{{ drawerContent?.voucherName?`${drawerContent?.voucherName}`:'未使用优惠券' }}</h5>
      <h5>总价：￥{{drawerContent?.totalPrice}}</h5>
 
    </template>
  </el-drawer>
  <!-- 分页 -->
  
    <el-pagination
     background layout="sizes,prev, pager, next,jumper" 
     :total="total" :page-sizes="[5,10, 20]" 
     :page-size="form.pageSize" 
     :current-page="form.page"
     @update:current-page="onPageChange"
     @update:page-size="onPageSizeChange"
     v-if="tabIndex === '1'"
     />
  

   
 
</template>    
<style scoped lang="less">
  //  待制作内容部分 
.infinite-list {
  height: 600px;
  padding: 0;
  margin: 0;
  list-style: none;
  .card-header{
    display: flex;
    justify-content: space-between;
  }
  .item{
    padding: 0 10px;
    border-radius: 15px;
    background: linear-gradient(to right, #FFFEFE, #FFECF0);
    .price{
      font-weight: bold;
    }
    .num{
      color: #545C64;
    }
  }
  .card-footer{
    display: flex;
    justify-content: space-between;
    .price{
      font-weight: bold;
    
    }
  }
  .item{
    margin-bottom: 15px;
    border-bottom: 1px solid #E4E7ED;
    display: flex;
    justify-content: space-between;
    align-items: center;
    img{
      width: 150px;
      height:150px;
    }
    &:last-child{
    margin-bottom: 0;
  }
  }
 
  
}
// 全部订单内容部分
.all{
 padding: 10px;
 .form{
  border-bottom: 1px solid #E4E7ED;
 }

}
.el-pagination{
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
}
  
.el-drawer{
  .goods{
    border-bottom: 1px solid #1F1F1F;
    max-height: 400px;
    overflow: scroll;
    .drawer-item{
      padding: 10px;
      margin: 10px;
      border-radius: 10px;
      border: 1px solid #545C64;
      display: flex;
      justify-content: space-between;
      align-items: center;
      img{
        width: 150px ;
        height: 150px;
      }
    }
  }
  .el-drawer__footer{
    h5{
      color: #82868A;
    }
  }
}

</style>
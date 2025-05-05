<script lang="ts" setup>
import type { ECOption } from '@/types/Echarts';
import { dayjs, type TabsPaneContext } from 'element-plus';
import {getcurDataAPI,getcurWeekDataAPI,getcurMonthDataAPI,getcurYearDataAPI} from '@/apis/Statistics'
import { ref,onMounted } from 'vue';
import type { DayCount, GoodsStatistics, OrdersStatistics, VoucherStatistics } from '@/types/Statistics';
//控制dialog的显示与隐藏
const dialogVisible=ref(false)
//用户选择的日期
const dateActive=ref('')
const shortcuts=[
  {
    text:'今日',
    value: new Date()
  }
]
onMounted(async()=>{
  //将日期默认选择为当日
  const now=new Date()
  dateActive.value=dayjs(now).format('YYYY-MM-DD')
  getcurData()
})
//卡片赋值
const cardSetValue=(data:OrdersStatistics[])=>{
  cardArr.value[0].value=data[0].orderCount
  cardArr.value[1].value=data[0].orderTotalPrice
  cardArr.value[2].value=data[1].orderCount
  cardArr.value[3].value=data[1].orderTotalPrice
}
//柱状图赋值
const goodsBarChartSerValue=(data:GoodsStatistics[])=>{
  //x轴赋值
 const xAxis= goodsSalesBarChartOption.value.xAxis as {data:string[]}
 xAxis.data=data.map(v=>v.goodsName)
 //y轴赋值
 const series= goodsSalesBarChartOption.value.series as {type:'bar',data:{value:number}[]}
 series.data.forEach(v=>v.value=0)
 if(data.length===0){
  series.data.forEach(v=>{
    v.value=0
  })
  return
 }
   for(let i=0;i<data.length;i++){
    series.data[i].value=data[i].goodsCount
   }
   
}
//饼状图区域赋值
const voucherUsedPieChartSerValue=(data:VoucherStatistics[])=>{
 
 const series= voucherUsedPieChartOption.value.series as {type:'pie',data:{value:number,name:string}[]}[]
 if(data.length===0){
  series[0].data=[]
  return
 }
  for(let i=0;i<data.length;i++){
    series[0].data[i]={
      value:data[i].voucherUsedCount,
      name:data[i].voucherName
    }
  }

}
//折线图赋值
const orderLineChartSetvalue=(data:DayCount[])=>{
  const xAxis= orderSalesLineChartOption.value.xAxis as {type:'category',data:string[]}
  const series= orderSalesLineChartOption.value.series as {type:'line',data:number[]}[]

  xAxis.data.forEach((v,i)=>{
  const index= data.findIndex(j=>j.day===v)
  if(index!==-1){
    series[0].data[i]=data[index].count
  }else{
    series[0].data[i]=0
  }
 
  })
 
  
}
//标签栏数组
const timeTabPane=[
  '当日',
  '当周',
  '当月',
  '当年',
  '日期选择'
]
//卡片数组
const cardArr=ref([
  {
    name:'自提订单量',
    color:'#F5F5F5',
    value:0
  },
  {
    name:'自提订单收入',
    color:'#90EE90',
    value:0
  },
  {
    name:'外送订单量',
    color:'#FFE4C4',
    value:0
  },
  {
    name:'外送订单收入',
    color:'#ADD8E6',
    value:0
  }
])
//商品销量柱状图option
const goodsSalesBarChartOption=ref<ECOption>({
  title:{
    text:'商品销量榜前五',
    left:'center',
    bottom:'5%',
    
  },
  tooltip:{
   trigger:'item'
  },
  xAxis:{
    data:[]
  },
  yAxis: {},
  series:{
    type:'bar',
    data:[
      {
        value:0,
        itemStyle:{
          color:'#1f77b4',
          borderType:'solid',
          opacity:0.7
        }
      },
      {
        value:0,
        itemStyle:{
          color:'#ff7f0e',
          borderType:'solid',
          opacity:0.7
        }
      },
      {
        value:0,
        itemStyle:{
          color:'#2ca02c',
          borderType:'solid',
          opacity:0.7
        }
      },
      {
        value:0,
        itemStyle:{
          color:'#d62728',
          borderType:'solid',
          opacity:0.7
        }
      },
      {
        value:0,
        itemStyle:{
          color:'#9467bd',
          borderType:'solid',
          opacity:0.7
        }
      }],
      itemStyle: {
        borderRadius:5,
        borderWidth: 1,
        borderType: 'solid',
        borderColor: '#73c0de',
       
      }
  }
  })
//优惠券使用量饼状图部分
const voucherUsedPieChartOption=ref<ECOption>({
  title: {
    text: '各优惠券使用比例',
    left: 'center',
    bottom: '4%'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  tooltip:{
   trigger:'item'
  },
  series:[
    {
      type:'pie',
      data:[
        
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})
//订单销量折线图
const orderSalesLineChartOption=ref<ECOption>({
  title:{
   text:'订单销量统计',
   left:'center',
   bottom:'4%'
  },
  xAxis:{
    type:'category',
    data:[],
   
   
  },
  yAxis:{
    type:'value'
  },
  series:[
    {
      data:[],
      type:'line'
    }
    
  ]
  })
const activeName=ref(0)
//点击标签栏时触发
const handlerTabClick=(tab:TabsPaneContext)=>{
 activeName.value=Number(tab.index)
 if(activeName.value===0){
  //获取今日数据
  getcurData()
 }else if(activeName.value===1){
  //获取本周数据
  getcurWeekData()
}else if(activeName.value===2){
  //获取本月数据
  getcurMonthData()
}else if(activeName.value===3){
  //获取本年数据
  getcurYearData()
 
}else{
   //日期选择
   selectDate()
}
}
const getcurData=async()=>{
    
 const res=await getcurDataAPI(dateActive.value)
 //卡片区域赋值
 cardSetValue(res.data.ordersStatistics)
//商品柱状图区域赋值
goodsBarChartSerValue(res.data.goodsStatistics)
//饼状图区域赋值
voucherUsedPieChartSerValue(res.data.voucherStatistics)
}
//获取本周的日期
const getcurWeekDate=(dateStr: string)=>{
  const dates = [];
    // 将传入的日期字符串转换为 Date 对象
    const today = new Date(dateStr);

    // 获取今天是本周的第几天（0 - 6，0 表示周日）
    const dayOfWeek = today.getDay();

    // 计算本周一的日期
    const firstDay = new Date(today);
    firstDay.setDate(today.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1));

    for (let i = 0; i < 7; i++) {
        const currentDate = new Date(firstDay);
        currentDate.setDate(firstDay.getDate() + i);

        // 将日期格式化为 YYYY-MM-DD
        const year = currentDate.getFullYear();
        const month = String(currentDate.getMonth() + 1).padStart(2, '0');
        const day = String(currentDate.getDate()).padStart(2, '0');
        dates.push(`${year}-${month}-${day}`);
    }

    return dates;
}
const getcurWeekData=async ()=>{
  //首先获取这一周内的日期
 const dates= getcurWeekDate(dateActive.value)
 //将这个日期渲染到option
 const xAxis= orderSalesLineChartOption.value.xAxis as {type:'category',data:string[]}
 xAxis.data=dates
 //查询数据
const res=await getcurWeekDataAPI(dateActive.value)
 //卡片区域赋值
 cardSetValue(res.data.ordersStatistics)
//商品柱状图区域赋值
goodsBarChartSerValue(res.data.goodsStatistics)
//饼状图区域赋值
voucherUsedPieChartSerValue(res.data.voucherStatistics)
//折线图区域赋值
orderLineChartSetvalue(res.data.orderCountByDay)

}
//获取本月的周日期
const getcurMonthOfWeekDate = (dateStr: string) => {
    // 将传入的日期字符串转换为 Date 对象
    const now = new Date(dateStr);
    const year = now.getFullYear();
    const month = now.getMonth();

    // 获取本月最后一天
    const lastDayOfMonth = new Date(year, month + 1, 0);
    const daysInMonth = lastDayOfMonth.getDate();

    const allDays = [];
    for (let day = 1; day <= daysInMonth; day++) {
        const formattedDay = String(day).padStart(2, '0');
        allDays.push(formattedDay);
    }

    return allDays;
};    
const getcurMonthData=async()=>{
  const MonthOfDays=getcurMonthOfWeekDate(dateActive.value)
   //将这些日期渲染到option
 const xAxis= orderSalesLineChartOption.value.xAxis as {type:'category',data:string[]}
 xAxis.data=MonthOfDays
 //获取数据
const res=await getcurMonthDataAPI(dateActive.value)
 //卡片区域赋值
 cardSetValue(res.data.ordersStatistics)
//商品柱状图区域赋值
goodsBarChartSerValue(res.data.goodsStatistics)
//饼状图区域赋值
voucherUsedPieChartSerValue(res.data.voucherStatistics)
//折线图区域赋值
orderLineChartSetvalue(res.data.orderCountByDay)
//渲染折线图
orderLineChartSetvalue(res.data.orderCountByDay)

}
//得到1-12月份的数组
const getMonth=()=>{
 const year= dateActive.value.substring(0,dateActive.value.indexOf('-'))
  const MonthArr:{name:string,code:number}[]=[]
  for(let y=1;y<=12;y++){
    MonthArr.push({
      name:`${year}年${y}月`,
      code:y
    })
  }
  return {
    MonthArr,
    year
  }
}
const getcurYearData=async()=>{
  //首先渲染折线图部分,即1,12月
 const {MonthArr,year}= getMonth()
 const xAxis= orderSalesLineChartOption.value.xAxis as {type:'category',data:string[]}
 xAxis.data=MonthArr.map(v=>v.name)
 //获取数据
 const res=await getcurYearDataAPI(year)
 console.log(res);
 cardSetValue(res.data.ordersStatistics)
 goodsBarChartSerValue(res.data.goodsStatistics)
 voucherUsedPieChartSerValue(res.data.voucherStatistics)
orderLineChartSetvalue(res.data.orderCountByDay)
}
//日期选择
const selectDate=()=>{
  dialogVisible.value=true
  
}
//当用户修改了要查看的日期
const onDateChange=()=>{
  //关闭弹层
  setTimeout(() => {
    dialogVisible.value=false
  }, 150);
  //将标签栏定位到当日
  activeName.value=0
  //获取当日的数据
  getcurData()
}
</script>
<template>
  <!-- 查看不同时间段的订单 -->
    <el-tabs type="card" v-model="activeName"  @tab-click="handlerTabClick">
    <el-tab-pane v-for="(item,index) in timeTabPane" :key="item" :label="item" :name="index"/>
  </el-tabs>
  <!-- 当前时间 -->
   <span class="currentTime">当前时间：{{dateActive}}</span>
  <!-- 订单量,订单平均金额 -->
  <div class="order-count">
   <ul>
    <li v-for="(item,index) in cardArr" :key="item.name" class="card" :style="{backgroundColor:item.color}">
      <h5>{{ item.name }}</h5>
      <h5>{{index%2===1?`￥${item.value}`:`${item.value}`}}</h5>
    </li>
   </ul>
  </div>
  <!-- 销量柱状图与饼图 -->
  <div class="sales-chart">
  <v-chart :option="goodsSalesBarChartOption" class="bar-chart"></v-chart>
  <v-chart class="pie-chart" :option="voucherUsedPieChartOption"></v-chart>
  </div>
 <!-- 销量趋势折线图，只有选中的标签栏为本周和本月时才显示 -->
  <div class="trend-chart" v-if="activeName>0&&activeName<=3">
   <v-chart :option="orderSalesLineChartOption" class="order-line-chart"/>
  </div>
  <!-- 日期选择dialog -->
   
  <el-dialog v-model="dialogVisible" title="日期选择" width="15%">
    <el-date-picker
        v-model="dateActive"
        type="date"
        placeholder="Pick a day"
        size="default"
         :shortcuts="shortcuts"
         @change="onDateChange"
         :clearable="false"
         value-format="YYYY-MM-DD"
      />
  </el-dialog>
</template>


<style scoped lang="less">
.order-count{
 
  padding-bottom: 10px;
  border-bottom: 3px solid #ccc;
  height: 110px;
  ul{
    display: flex;
    justify-content: space-around;
    height: 100%;
    .card{
     transition: all 0.3s;
      width: 200px;
      border-radius: 15px;
      h5{
        line-height: 40px;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            /* 字体颜色 */
            color: #333;
            text-align: center;
            /* 字体加粗 */
            font-weight: 600;
            /* 文字倾斜 */
            font-style: italic;
            /* 文字阴影 */
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
            
            /* 字母间距 */
            letter-spacing: 0.05em;
         
      }
      &:hover{
        transform: translateX(-5px) translateY(-5px);
        box-shadow: -4px -4px 3px rgba(0, 0, 0, 0.5) inset ;
      }
    }
  }
}
//商品销量柱状图部分
.sales-chart{
  display: flex;
  height: 350px;  
  .bar-chart{
    width: 600px;
  }
  .pie-chart{
    flex: 1;
    
  }
}
//趋势折线图
.trend-chart{
  height: 300px;
}
//当前时间
.currentTime{
  position: fixed;
  left: 600px;
  top: 60px;
}
</style>
<script lang="ts" setup>
import { computed, ref } from 'vue';
import { onShow,onLoad } from '@dcloudio/uni-app';
import { getGoodsCategoryAPI,getGoodsListAPI,checkGoodsHasSkuAPI } from '@/apis/goods';
import { addToCartAPI,getCartListAPI,clearCartListAPI } from '@/apis/user';
import type { GoodsCategory } from '@/types/GoodsCategory';
import type {Goods} from '@/types/Goods';
import { useUserStore } from '@/stores/user';
import GoodsCount from './components/GoodsCount.vue';
import type { UserCart } from '@/types/user';
import { useCartStore } from '@/stores/cart';
import { useAddressStore } from '@/stores/address';
import { useShopStore } from '@/stores/shop';
const shopStore=useShopStore()
const userStore=useUserStore()
const cartStore=useCartStore()
const addressStore=useAddressStore()
//购物车列表数据
const cartList=ref<UserCart[]>([])
//商品分类
const Goodscategory=ref<GoodsCategory[]>([])
//商品数据
const GoodsList=ref<Goods[]>([])
//获取屏幕的安全区域
const {safeAreaInsets} = uni.getSystemInfoSync();
// 选择自提或外送
const userSelectedWay = ref('');
//选中的分类Id
const selectedCategoryId = ref('');
// 滚动到的分类 ID
const scrollToCategoryId = ref('category-' + selectedCategoryId.value);

//控制购物车Dialog的弹出与收回
const cartDialogInvisible = ref(false);
//存储最外层的滚动条高度
let goodsListTop:number
//存储商品滚动条触发分类变化的临界高度
let scrollHeight:number[]=[]
//用户的经纬度
const userLocation=ref<{longitude:number,latitude:number}>({
  longitude:0,
  latitude:0
})
//直线距离
const lineDistance=ref(0)
//修改购买方式
const changeBuyWay=(way:'pick-up'|'delivery')=>{
  userStore.setBuyWay(way)
  userSelectedWay.value=userStore.buyWay
}
const getData=async()=>{
  const categoryRes=await getGoodsCategoryAPI()
 Goodscategory.value=categoryRes.data
 selectedCategoryId.value=Goodscategory.value[0].id
 const goodsRes=await getGoodsListAPI()
 GoodsList.value=goodsRes.data
 GoodsList.value.forEach(v=>v.number=0)
 
}
//计算两个经纬度之间的直线距离
function calculateDistance(lat1:number, lon1:number, lat2:number, lon2:number) {
  
  
    const R = 6371; // 地球半径，单位为千米
    const dLat = (lat2 - lat1) * (Math.PI / 180);
    const dLon = (lon2 - lon1) * (Math.PI / 180);
    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1 * (Math.PI / 180)) * Math.cos(lat2 * (Math.PI / 180)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = R * c;
    return distance;
}

//获取直线距离
const getDistance=async()=>{
  let distance
  //如果用户没有选择店铺,直接return
  if(!shopStore.shopActive.id) return
  
  //获取用户的当前位置
 const res=await new Promise((resolve,reject)=>{
  uni.getFuzzyLocation({
    type:'wgs84',
    success:async(res)=>{ 
      
      //纬度
    userLocation.value.latitude= res.latitude
      //经度
    userLocation.value.longitude=res.longitude
      //获取用户选择的店铺地址
      //店铺的经度
     const shopLongitude=shopStore.shopActive.longitude
     //店铺的纬度
     const shopLatitude=shopStore.shopActive.latitude
     //计算经纬度
   resolve(calculateDistance(userLocation.value.latitude,userLocation.value.longitude,shopLatitude,shopLongitude)) 
    
    }

  })
 }) 
  distance=Number(res)
  return distance
}
//获取购物车数据
const getCartData=async()=>{

  const res=await getCartListAPI()
  
      cartList.value=res.data
    
        GoodsList.value.forEach(v=>v.number=0)
        cartList.value.forEach(item=>{
        //给商品列表对应的商品赋count
       GoodsList.value.forEach(v=>{
        if(v.id===item.goodsId){
          v.number=item.count+v.number!
        }

       })
      })
      //存储到pinia
      cartStore.setCartList(cartList.value)

      }
onLoad(async()=>{

  await getData()

   /* 获取 .goods-list 和 .item-of-category 的位置信息 */
 const query = uni.createSelectorQuery();
  // 获取 .goods-list 的位置信息
  query.select('.goods-list').boundingClientRect((goodsListRect) => {
    if (!goodsListRect) return;
    const gr=goodsListRect as UniApp.NodeInfo
    goodsListTop=gr.top!
  }).exec();

  query.selectAll('.item-of-category').boundingClientRect((res) => {
    const r=res as UniApp.NodeInfo[]
    r.forEach(item=>{
      scrollHeight.push(item.bottom!-goodsListTop)
    })
  }).exec();
  
})

onShow(async()=>{
  userSelectedWay.value=userStore.buyWay
  //如果用户登录了,就获取购物车数据
  if(userStore.userInfo!.token){
   await getCartData()
  }
  const distance=await getDistance()
  
  if(distance) lineDistance.value= Math.floor(distance)*1000
  
})
//搜索
const toSearch=()=>{
  uni.navigateTo({
    url: '/packageUser/pages/search/search'
  })
}
//选择商品分类后触发
const onSelectedCategory=(id:string)=>{
  selectedCategoryId.value=id
  scrollToCategoryId.value = 'category-' + id;

}
//控制左侧滚动条移动
const scrollMove=computed(()=>{
  return Goodscategory.value.findIndex(v=>v.id===selectedCategoryId.value)
})
//加入购物车
const addToCart=async(id:string)=>{
  if(!userStore.userInfo!.token){
   return uni.showModal({
      title:'温馨提示',
      content:'请先登录',
      cancelText:'再逛逛',
      confirmText:'去登录',
      success:(success)=>{
        if(success.confirm){
          uni.navigateTo({
            url:'/packageUser/pages/login/login'
          })

        }
      },
    })
  }
  //检查该商品是否包含sku
 const res=await checkGoodsHasSkuAPI(id)
 if(res.data){
  //说明该商品是有sku的,跳转到商品详情页
  uni.navigateTo({url:`/pages/shop/goodsDetail/goodsDetail?id=${id}`,})
 }else{
  //说明该商品没有sku,直接加入购物车
 await addToCartAPI({
   count:1,
   goodsId:id,
   price:Number(GoodsList.value.find(v=>v.id===id)!.price),
  })
  await getCartData()
  
 }
  
}
//减少购物车商品数量
const reduceToCart=async()=>{
  //显示弹层
  cartDialogInvisible.value=true
}
//加入购物车
const add=async(id:string)=>{
  //根据购物车id对商品进行修改
 await addToCartAPI({
  id,
  count:1
 })
 await getCartData()
 
}
//减少购物车商品数量
const reduce=async(id:string,goodsId:string)=>{
  await addToCartAPI({
  id,
  count:-1
 })
 await getCartData()
 if(!cartList.value.find(v=>v.goodsId===goodsId)){
  GoodsList.value.find(v=>v.id===goodsId)!.number=0
 }
 if(!cartList.value.length){
   //没有数据了,关闭弹层
    cartDialogInvisible.value=false
 }
}
//清空购物车
const clearCartList=()=>{
   uni.showModal({
    content:'确定清空购物车吗？',
    cancelText:'我再想想',
    cancelColor:'#868686',
    confirmColor:'#E91F2F',
    success:async(res)=>{
      if(res.confirm){
        await clearCartListAPI()
        await getCartData()
        cartDialogInvisible.value=false
      }
    }
   })
}
//点击显示购物车Dialog,显示购物车详情
const showCartDetail=()=>{
  cartDialogInvisible.value=!cartDialogInvisible.value
}
//监听滚动事件
const handlerScroll=(e:any)=>{
   //滚动的距离
   const scrollTop=e.detail.scrollTop
   let currentIndex = 0;
  for (let i = 0; i < scrollHeight.length - 1; i++) {
    if (scrollTop >= scrollHeight[i] && scrollTop < scrollHeight[i + 1]) {
      currentIndex = i+1;
      break;
    }
  }
 
  // 更新选中的分类Id
  selectedCategoryId.value = Goodscategory.value[currentIndex].id;
}
//此computed用于计算购物车商品总价
const total=computed(()=>{
  return cartList.value.reduce((pre,cur)=>{
    return pre+cur.price*cur.count
  },0)
})
//去结算
const goPay=()=>{
  uni.navigateTo({
    url:'/packageUser/pages/orderSettlement/orderSettlement?type=cart'
  })
}
//跳转到商品详情页
const goodsDetail=(id:string)=>{
  uni.navigateTo({url:`/pages/shop/goodsDetail/goodsDetail?id=${id}`,})
  
}
//跳转到选择收货地址页面
const selectAddress=()=>{
  if(!userStore.userInfo.token){
     uni.showModal({
      title:'温馨提示',
      content:'请先登录',
      cancelText:'再逛逛',
      confirmText:'去登录',
      success:(success)=>{
        if(success.confirm){
          uni.navigateTo({
            url:'/packageUser/pages/login/login'
          })

        }
      },
    })
    return
  }
  uni.navigateTo({
    url:'/packageUser/pages/address/address'
  })
}
//选择店铺
const selectShop=()=>{
  uni.navigateTo({
    url:'/packageUser/pages/pickup/pickup'
  })
}
</script>
<template>
  <view class="shop">
  <!-- 自定义导航栏 -->
  <view class="navigation" :style="{paddingTop:(safeAreaInsets!.top+5)+'px'}">
    <!-- 购物方式 -->
  
    <view class="way" :class="{'selected-way':userSelectedWay==='pick-up'}" @tap="changeBuyWay('pick-up')">自提</view>
    <view class="way"  :class="{'selected-way':userSelectedWay==='delivery'}" @tap="changeBuyWay('delivery')">外送</view>
    <!-- 搜索 -->
    <view class="search">
       <!-- 搜索图标 -->
        <view class="icon">
          <uni-icons type="search" size="20" class="search-icon"></uni-icons>
        </view>
  <text @tap="toSearch">
  搜索好奶
  </text>
    </view>
  </view>
  <!-- 自提 -->
  <view class="position" v-if="userSelectedWay==='pick-up'">
    <view class="place">
      <uni-icons custom-prefix="iconfont" type="icon-naicha" size="18"></uni-icons>
      <text @tap="selectShop">{{shopStore.shopActive?.id?`${shopStore.shopActive!.name}〉`:'请选择店铺&nbsp;〉'}}</text>
    </view>
    <view class="distance"> 
      <uni-icons custom-prefix="iconfont" type="icon-location-act" size="18"></uni-icons>
        <text v-if="lineDistance">直线距离{{lineDistance}}m</text> 
    </view>
  </view>
<!-- 外送 -->
  <view class="position" v-else>
    <view class="address" @tap="selectAddress">
      <uni-icons type="home" size="20"></uni-icons>
      <text>{{addressStore.addressActive?.id?`${addressStore.addressActive!.address} ${addressStore.addressActive!.detail} 〉`:'请选择收货地址 〉'}}</text>
    </view>
    <view class="distance"> 
      <uni-icons custom-prefix="iconfont" type="icon-location-act" size="18"></uni-icons>
        <text @tap="selectShop">{{shopStore.shopActive?.id?`${shopStore.shopActive!.name}〉`:'请选择店铺&nbsp;〉'}}</text> 
    </view>
  </view>

  <!-- 商品区域 -->
 <view class="goods">
  <!-- 侧边商品分类 -->
    <scroll-view scroll-y class="goods-category">
      <!-- 侧边滚动条 -->
      <view class="scroll-bar" :style="{transform:`translateY(${scrollMove*100}rpx)`}">

      </view>
      <view class="items">
        <!-- 每一项分类 -->
        <view v-for="item in Goodscategory" :key="item.id" class="item" @tap="onSelectedCategory(item.id)" :class="{'goods-category-active':selectedCategoryId===item.id}">
         <text>{{ item.name }}</text>
        </view>
      </view>
     
    </scroll-view>
    <!-- 右侧商品区 -->
     <scroll-view scroll-y class="goods-list" :scroll-into-view="scrollToCategoryId" @scroll="handlerScroll" scroll-with-animation>
      <!-- 每一项分类下的商品 -->
      <view class="item-of-category" v-for="category in Goodscategory" :key="category.id"  :id="'category-' + category.id">
        <view class="top">
    <!-- 分类名称 -->
    <text class="category-name">{{ category.name }}</text>
         <!--分类图片  -->
         <image :src="category.image" mode="heightFix" class="category-image"/> 
        </view>
    
        <!-- 商品列表 -->
        <view class="item" v-for="g in GoodsList.filter(v=>v.categoryId===category.id)" :key="g.id" @tap="goodsDetail(g.id)">
            <!-- 商品图片 -->
             <view class="left">
              <image :src="g.coverImage" mode="scaleToFill"/>
             </view>
            <view class=right>
               <view class="top">
               <!-- 商品名称 -->
               <text class="gname">{{ g.name }}</text>
               <!-- 商品描述 -->
               <text class="gdesc">{{ g.introduction }}</text>
              </view>
         
               <!--商品价格与购买按钮 -->
               <view class="bottom">
               <text>￥{{ g.price }}起</text>
              
                  <GoodsCount :goodsId="g.id" :goodsNumber="g.number!" @add-to-cart="addToCart" @reduce-to-cart="reduceToCart"></GoodsCount>
              </view>
            </view>
             
          </view>
          
      </view>
      <view style="height: 100rpx; background-color: #FFFFFF;"></view>
     </scroll-view>
 </view>
 <!-- 底部购物车 -->
  <view class=cart v-if="cartList.length>0">
    <!-- 左侧购物车角标 -->
  <view class="subscript" @tap="showCartDetail">
    <!-- 数字角标 -->
    <uni-badge class="uni-badge-left-margin num-subscript" :text="cartList.reduce((pre,cur)=>pre+cur.count,0)" :customStyle="{background: '#FF9F00'}"/>
  </view>
  <!-- 右侧价格支付 -->
  <view class="go-settle">
    <text style="font-size: 26rpx;">合计</text><text class="price-symbol">￥</text> <text class="price">{{total}}</text>
     <!--  MP-WEIXIN -->
  <button class="toPay" @tap="goPay">去结算</button>
  </view>

  </view>
    <!-- 购物车详情Dialog -->
    <view class="cart-dialog" :class="{'cartDialogVisible':!cartDialogInvisible}">
      <!-- 顶部清空购物车按钮 -->
      <view class="top">
        <text>已选商品</text>
        <!-- 清空按钮 -->
      <uni-icons type="trash" size="15" @tap=clearCartList>清空</uni-icons>
      </view>
      <scroll-view scroll-y class="goods-item">
        <view class="item" v-for="item in cartList" :key="item.id" v-if="GoodsList.length > 0">
       <!-- 左边图片区域 -->
         <view class="left">
          <image
            :src="item.coverImage"
            mode="scaleToFill"
          />
        </view>
        <!-- 右边商品区域 -->
        <view class="right">
          <!-- 左侧商品信息 -->
           <view class="info">
          <text class="name">{{ item.goodsName }}</text> 
          <text class="sku" v-if="item.skuInfo">{{ item.skuInfo }}</text>
          <text >￥{{ item.price }}</text>
           </view>
           <!-- 右侧商品数量 -->
            <view>
             <GoodsCount :goodsId="item.id!" :goodsNumber="item.count!" @add-to-cart="add(item.id!)" @reduce-to-cart="reduce(item.id!,item.goodsId)"></GoodsCount>
            </view>
        </view>
        </view>
     
      </scroll-view>
      
    </view>
  </view>
 <!-- 灰色蒙层 -->
  <view class="modal" v-if="cartDialogInvisible" @tap="cartDialogInvisible=false"/>
</template>
<style scoped lang="less">

 .shop{
  position: relative;
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  view,text{
    box-sizing: border-box;
  }
} 
 //导航栏区域
.navigation{
    display: flex;
    align-items: center;
    height: 120rpx;
    .way{
      transition: all 0.3s;
      font-size: 35rpx;
      color: gray;
      margin: 0 40rpx 0 10rpx;
      }
      // 用户选择的方式:自提或外送
.selected-way{
  color: #2d2d2e;
  font-size: 48rpx;
}
    .search{
       height: 70rpx;
      display: flex;
      align-items: center;
     
      width: 250rpx;
      border-radius: 30rpx;
      background-color: #F3F3F7;
      text{
        display: block;
        line-height: 70rpx;
        flex: 1;
       
        margin-left: 20rpx;
        font-size: 25rpx;
        letter-spacing: 6rpx;
        color: darkgrey;
      }
    }
}
//定位区域
.position{
  height: 100rpx;
  margin-top: 40rpx;
  margin-bottom: 20rpx;
  .place{
    font-size: 38rpx;
    color: #333333;
    font-weight: bold;
  }
  .address{
    font-size: 35rpx;
    color: #333333;
    text{
      font-weight: bold;
      margin-left: 10rpx;
    }
  }
  .distance{
    text{
      font-size: 30rpx;
      margin-left: 15rpx;
    }
  }
  text{
    margin-left: 12rpx;
  }
}
//商品区域
.goods{
 
  flex: 1;
  display: flex;
  
 .goods-category{
  
  position: relative;
 
   height: 958rpx;
  width: 200rpx;
  background-color: #F8F8F8;
    .scroll-bar{
      position: absolute;
      transition: all 0.5s;
      width: 10rpx;
      height: 100rpx;
      background-color: #FF192B;
    }
    .items{
     
      .item{
        color: #777777;
        font-size: 24rpx;
    height: 100rpx;
    text-align: center;
    line-height: 100rpx;
  }
    }
 
 }
 .goods-list{
 
  height: 958rpx;
 
  .top{
    .category-image{
    display: block;
    margin: 25rpx 0;
    border-radius: 20rpx;
    
    width: 100% !important;
    height: 90rpx;
  }
  
  text{
    display: inline-block;
    height: 40rpx;
    margin: 20rpx;
    color: #4C4C4C;
  }
  }
 
  .item{
    height: 250rpx;
    display: flex;
    .left{
      padding: 20rpx;
      width: 40%;
      image{
      width: 200rpx;
      height: 200rpx;
      border-radius: 50%;
    }
    }
    .right{
    padding-left: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      flex: 1;
      .top{
        display: flex;
        flex-direction: column;
        text{
        margin: 0;
      }
      .gname{
        font-weight: bold;
        color: #4C4C4C;
      }
      .gdesc{
        margin-top: 10rpx;
        font-size: 26rpx;
        color: #AAAAAA;
      }
      }
     text{
      margin: 0;
     }
     .bottom{
      display: flex;
      justify-content: space-between;
  
    
     }
     
    }
 
  }
 }
}
//底部购物车部分
.cart{
 z-index: 9999;
  padding:0 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100rpx;
  background-color: #FFFFFF;
  position: fixed;
  bottom: 0;
 .subscript{
  position: relative;
     width: 80rpx;
     height: 100rpx;
    background: url('@/static/cart.png') no-repeat center center / 85% ;
    .num-subscript{
      position: absolute;
      top: 0;
      right: 0;
    }
 }
 .go-settle{
  display: flex;
  align-items: center;
  .toPay{
    margin-left: 20rpx;
    line-height: 80rpx;
    width: 230rpx;
    height: 80rpx;
    background-color: #E60012;
    color: #FEF3F8;
    border-radius: 20rpx;
  }
  .price-symbol{
    font-weight: bold;
    font-size: 26rpx;
  }
  .price{
    font-size: 35rpx;
    font-weight:bold;
  }
 }
}
// 购物车详情Dialog
.cart-dialog{
  padding: 20rpx;
  z-index: 9999;
  transition: all 0.4s;
  position: absolute;
  width: 100%;
  bottom: 100rpx;
  max-height: 750rpx;
  background-color: #FFFFFF;
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  .top{
    height: 50rpx;
    display: flex;
    justify-content: space-between;
    text{
      font-weight:900;
      font-size: 34rpx;
    }
  }
 .goods-item{
   max-height: 700rpx;
   .item{
    display: flex;
    margin: 40rpx 0;
    .left{
      width: 25%;
      image{
        width: 120rpx;
        height: 120rpx;
        border-radius: 50%;
      }
    }
    .right{
      flex: 1;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .info{
        display: flex;
        flex-direction: column;
        .name{
          font-size: 30rpx;
          font-weight: bold;
        }
        .sku{
          font-size: 22rpx;
          color: #878787;
        }
      }
    }
   }
 }
}
.goods-category-active{
  background-color: #FFFFFF;
}
// 控制购物车详情Dialog的弹出与隐藏
.cartDialogVisible{
  transform: translateY(850rpx);
}
//灰色蒙层
.modal{
  width: 100%;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  background: #000;
  opacity: 0.5;
  overflow: hidden;
  z-index: 9000;
  
}
</style>
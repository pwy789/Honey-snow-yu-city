<script lang="ts" setup>


const props=defineProps<{
  goodsId:string,
  goodsNumber:number
}>()
const emit = defineEmits(['addToCart','reduceToCart'])
</script>
<template>
  <view class="operate">
      <!-- 减按钮 -->
   <view class="reduce-btn num-btn" v-if="props.goodsNumber>0" @tap.stop="emit('reduceToCart',props.goodsId)">-</view>
   <text v-if="props.goodsNumber!>0">{{ props.goodsNumber }}</text>
    <!-- 加按钮 -->
     <view class="add-btn num-btn" @tap.stop="emit('addToCart',goodsId)">+</view>
      </view> 
</template>
<style lang="less">
    .operate{
      display: flex;
      .add-btn{
      background-color: #E60012;
      color: #FFFFFF;
     }
     .reduce-btn{
      border: 1rpx solid  #E60012;
      color: #E60012;
      font-size: 48rpx;
     }
     }
     .num-btn{
      display: flex;
      justify-content: center;
      line-height: 33rpx;
      width: 40rpx;
      height: 40rpx;
      border-radius: 50%;
      margin: 0 20rpx;
     }
</style>
package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.OrdersGoods;
import com.pwy.mapper.GoodsMapper;
import com.pwy.mapper.OrdersGoodsMapper;
import com.pwy.service.OrdersGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrdersGoodsServiceImpl extends ServiceImpl<OrdersGoodsMapper, OrdersGoods> implements OrdersGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Transactional
    @Override
    public void saveOrdersGoods(List<OrdersGoods> ogs) {
        saveBatch(ogs);
    }

    @Override
    public void restoreBalanceById(String id) {
        //查询出该订单包含的所有商品
        List<OrdersGoods> ogs = lambdaQuery().eq(OrdersGoods::getOrdersId, id).list();
        //恢复库存
        Map<String,Integer> goodsMap=new ConcurrentHashMap<>();
        for (OrdersGoods og : ogs) {
             goodsMap.put(og.getGoodsId(),og.getCount());
        }
        goodsMapper.restoreBalance(goodsMap);

    }

    @Override
    public List<OrdersGoods> getOrdersGoodsByOrderId(String id) {

      return    list(Wrappers.<OrdersGoods>lambdaQuery().eq(OrdersGoods::getOrdersId,id));
    }
}

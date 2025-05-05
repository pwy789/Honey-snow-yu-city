package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.Cart;
import com.pwy.mapper.CartMapper;
import com.pwy.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Transactional
    @Override
    public void addToCart(Cart cart) {
        LambdaQueryWrapper<Cart> lqw=new LambdaQueryWrapper<>();
        if(cart.getGoodsId()!=null){
            lqw.eq(Cart::getGoodsId,cart.getGoodsId());
        }
        //根据购物车id对商品进行修改
        if(cart.getId()!=null){
            lqw.eq(Cart::getId,cart.getId());
        }

        if(cart.getSkuInfo()!=null){
            //表示该商品有sku
            lqw.eq(Cart::getSkuInfo,cart.getSkuInfo());
        }
        Cart c = getOne(lqw);
        if(c!=null){
            //如果该商品的id已经存在,并且skuInfo相同,那么直接增加数量就行,即使商品的id已经存在,但是skuInfo不同,也是不同的商品
            Integer count = c.getCount();
            if(count==1&&cart.getCount()==-1){
                //删除
                remove(lqw);
                return;
            }
            //更新
            c.setCount(count+cart.getCount());
            updateById(c);
            return;
        }
        save(cart);
    }

    @Override
    public List<Cart> getCartList(String userId) {


      return list(Wrappers.<Cart>lambdaQuery().eq(Cart::getUserId, userId));
    }
}

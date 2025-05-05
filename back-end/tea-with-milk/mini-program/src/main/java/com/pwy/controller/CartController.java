package com.pwy.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Cart;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.vo.CartVo;
import com.pwy.service.CartService;
import com.pwy.service.GoodsService;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private GoodsService goodsService;
    //将商品加入购物车
    @PostMapping
    public Result addToCart(@RequestBody Cart cart){
        log.info("cart :{}",cart);
        String userId =(String) ThreadLocalUtils.get();
        cart.setUserId(userId);
        cartService.addToCart(cart);
        return Result.success();
    }
    //获取购物车列表数据
    @GetMapping("/list")
    public Result cartList(){
        String userId =(String) ThreadLocalUtils.get();
        List<Cart> cartList = cartService.getCartList(userId);
        List<CartVo> result=new ArrayList<>();
        for (Cart cart : cartList) {
            String goodsId = cart.getGoodsId();
            Goods goods = goodsService.getById(goodsId);
            if(goods!=null){
                CartVo cartVo = BeanUtil.copyProperties(cart, CartVo.class);
                cartVo.setCoverImage(goods.getCoverImage());
                cartVo.setGoodsName(goods.getName());
                result.add(cartVo);
            }

        }
        return Result.success(result);
    }
    //清空购物车
    @DeleteMapping("/clear")
    public Result clearCartList(){

        Object userId = ThreadLocalUtils.get();
        cartService.remove(Wrappers.<Cart>lambdaQuery().eq(Cart::getUserId,userId));
        return Result.success();
    }
}

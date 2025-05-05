package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.pojo.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    void addToCart(Cart cart);

    List<Cart> getCartList(String userId);
}

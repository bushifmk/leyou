package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.utils.UserContainer;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 15:06
 * @description TODO
 **/
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "cart:uid:";

    public void addCart(Cart cart) {
        String key = KEY_PREFIX + UserContainer.getUser().getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        String hashKey = cart.getSkuId().toString();
        if (hashOps.hasKey(hashKey)) {
            String json = hashOps.get(hashKey);
            Cart cacheCart = JsonUtils.toBean(json, Cart.class);
            cart.setNum(cacheCart.getNum() + cart.getNum());
        }
        hashOps.put(hashKey, JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList() {
        String key = KEY_PREFIX + UserContainer.getUser().getId();
        if (!redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        if (hashOps.size() == 0) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        List<String> list = hashOps.values();
        return list.stream().map(str -> JsonUtils.toBean(str, Cart.class)).collect(Collectors.toList());
    }

    public void updateCartNum(Long skuId, int num) {
        String key = KEY_PREFIX + UserContainer.getUser().getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        String json = hashOps.get(skuId.toString());
        Cart cacheCart = JsonUtils.toBean(json, Cart.class);
        cacheCart.setNum(num);
        hashOps.put(skuId.toString(), JsonUtils.toString(cacheCart));
    }

    public void deleteCart(Long skuId) {
        String key = KEY_PREFIX + UserContainer.getUser().getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        hashOps.delete(skuId.toString());
    }

    public void cartCount(List<Cart> carts) {
        String key = KEY_PREFIX + UserContainer.getUser().getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        Map<String, String> map = hashOps.entries();
        List<Cart> loginCarts = map.values().stream().map(str -> JsonUtils.toBean(str, Cart.class)).collect(Collectors.toList());
        ArrayList<Cart> carts1 = new ArrayList<>();
        for (Cart cart : carts) {
            if(hashOps.hasKey(cart.getSkuId().toString())){
                Cart cart1 = JsonUtils.toBean(hashOps.get(cart.getSkuId().toString()), Cart.class);
                loginCarts.remove(cart1);
                cart1.setNum(cart1.getNum()+cart.getNum());
                carts1.add(cart1);
            }else {
                carts1.add(cart);
            }
        }
        loginCarts.addAll(carts1);
        for (Cart loginCart : loginCarts) {
            hashOps.put(loginCart.getSkuId().toString(), JsonUtils.toString(loginCart));
        }
    }
}

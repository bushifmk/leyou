package com.leyou.cart.web;


import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 15:04
 * @description TODO
 **/
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(){
        return ResponseEntity.ok(cartService.queryCartList());
    }
    @PutMapping
    public ResponseEntity<Void> updateCartNumBySkuId(@RequestParam("id")Long skuId,@RequestParam("num")int num){
        cartService.updateCartNum(skuId,num);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCartBySkuId(@PathVariable("id")Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("batch")
    public ResponseEntity<Void> cartCount(@RequestBody List<Cart> carts){
        cartService.cartCount(carts);
        return ResponseEntity.ok().build();
    }
}

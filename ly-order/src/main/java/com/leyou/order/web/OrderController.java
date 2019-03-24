package com.leyou.order.web;

import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDTO;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 22:02
 * @description TODO
 **/
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        Long orderId= orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
    @GetMapping("{id}")
    public ResponseEntity<Order> queryById(@PathVariable("id")Long id){
        return ResponseEntity.ok(orderService.queryById(id));
    }
    @GetMapping("url/{id}")
    public ResponseEntity<String> generateUrl(@PathVariable("id")Long orderId){
        return ResponseEntity.ok(orderService.createPayUrl(orderId));
    }
    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryPayState(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.queryPayStatus(orderId));
    }
}

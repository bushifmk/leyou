package com.leyou.order.web;

import com.leyou.order.pojo.OrderDTO;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

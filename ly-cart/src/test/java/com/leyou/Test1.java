package com.leyou;

import com.leyou.cart.utils.UserContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/24 21:59
 * @description TODO
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test1 {
    @Autowired
    private AmqpTemplate amqpTemplate;

   @Test
    public void deleteCart(){
        amqpTemplate.convertAndSend("ly.order.exchange","order.verify.code","nihao");
    }
}

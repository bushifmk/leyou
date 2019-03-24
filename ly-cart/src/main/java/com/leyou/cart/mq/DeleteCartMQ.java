package com.leyou.cart.mq;


import com.leyou.cart.utils.UserContainer;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/24 21:21
 * @description TODO
 **/
@Component
public class DeleteCartMQ {
    private static final String KEY_PREFIX = "cart:uid:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "ly.order.exchange",type = ExchangeTypes.TOPIC),
            key = "order.verify.code"
    ))
    public void deleteCart(HashMap<Long, List<Long>> map){
        Set<Map.Entry<Long, List<Long>>> entries = map.entrySet();
        Long id=0L;
        List<Long> skuIds=null;
        for (Map.Entry<Long, List<Long>> entry : entries) {
            id = entry.getKey();
            skuIds = entry.getValue();
        }
        String key = KEY_PREFIX + id.toString();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        for (Long skuId : skuIds) {
            hashOps.delete(skuId.toString());
        }
    }
}

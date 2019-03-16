package com.leyou.mq;

import com.leyou.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/16 16:32
 * @description TODO
 **/
@Component
public class ItemListener {
    @Autowired
    private SearchService searchService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.up.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = "item.up"
    ))
    public void listenItemUp(Long id){
        if(id!=null){
            searchService.createIndex(id);
        }
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.down.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = "item.down"
    ))
    public void listenItemDown(Long id){
        if(id!=null){
            searchService.deleteIndex(id);
        }
    }
}

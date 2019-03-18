package com.leyou.sms.mq;

import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtil;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/18 17:10
 * @description TODO
 **/
@Component
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private SmsProperties prop;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "ly.sms.exchange",type = ExchangeTypes.TOPIC),
            key = "sms.verify.code"
    ))
    public void listenVerifyCode(Map<String,String> msg){
        if(msg!=null&&msg.containsKey("phone")){
            String phone = msg.remove("phone");
            smsUtil.sendSms(phone,prop.getSignName(), prop.getVerifyTemplateCode(), JsonUtils.toString(msg));
        }
    }
}

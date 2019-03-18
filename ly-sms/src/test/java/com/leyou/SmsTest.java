package com.leyou;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/18 17:26
 * @description TODO
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void sendSms() throws InterruptedException {
        Map<String,String> msg=new HashMap<>();
        msg.put("phone", "18455823381");
        int num = new Random().nextInt(100000);
        System.out.println(num);
        msg.put("code", String.valueOf(num));
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
        Thread.sleep(1000L);
    }
}

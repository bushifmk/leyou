package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/15 16:13
 * @description TODO
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LyPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class,args);
    }
}

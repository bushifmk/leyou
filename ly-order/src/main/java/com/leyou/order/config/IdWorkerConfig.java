package com.leyou.order.config;

import com.leyou.common.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 22:33
 * @description TODO
 **/
@Configuration
public class IdWorkerConfig {
    @Bean
    public IdWorker idWorker(IdWorkerProperties prop){
        return new IdWorker(prop.getWorkerId(),prop.getDataCenterId());
    }
}

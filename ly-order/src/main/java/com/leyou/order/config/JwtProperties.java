package com.leyou.order.config;


import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/19 18:59
 * @description TODO
 **/
@Slf4j
@Data
@Component
@ConfigurationProperties("ly.jwt")
public class JwtProperties {
    private String pubKeyPath;
    private String cookieName;
    private PublicKey publicKey;
    @PostConstruct
    public void init(){
         try {
             publicKey = RsaUtils.getPublicKey(pubKeyPath);
         }catch (Exception e){
             log.error("【网关服务】密钥加载失败!");
             throw new RuntimeException("服务启动失败!",e);
         }

    }
}

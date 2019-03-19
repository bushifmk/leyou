package com.leyou.auth.config;


import com.leyou.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
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
    private String priKeyPath;
    private int expire;
    private String cookieName;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    @PostConstruct
    public void init(){
         try {
             privateKey = RsaUtils.getPrivateKey(priKeyPath);
             publicKey = RsaUtils.getPublicKey(pubKeyPath);
         }catch (Exception e){
             log.error("【授权中心】密钥加载失败!");
             throw new RuntimeException("服务启动失败!",e);
         }

    }
}

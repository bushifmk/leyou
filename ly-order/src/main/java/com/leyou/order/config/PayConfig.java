package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/24 18:39
 * @description TODO
 **/
@Data
public class PayConfig implements WXPayConfig {
    private String appID; // 公众账号ID

    private String mchID; // 商户号

    private String key; // 生成签名的密钥

    private int httpConnectTimeoutMs; // 连接超时时间

    private int httpReadTimeoutMs;// 读取超时时间

    private String notifyUrl;

    @Override
    public InputStream getCertStream() {
        return null;
    }
}

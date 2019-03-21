package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/21 16:14
 * @description TODO
 **/
@Data
@Component
@ConfigurationProperties("ly.filter")
public class FilterProperties {
    private List<String> allowPaths;
}

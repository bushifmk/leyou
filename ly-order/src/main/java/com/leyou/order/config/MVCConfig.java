package com.leyou.order.config;

import com.leyou.order.interceptors.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 19:06
 * @description TODO
 **/
@Configuration
public class MVCConfig implements WebMvcConfigurer {
    @Autowired
    private JwtProperties prop;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(prop)).addPathPatterns("/**");
    }
}

package com.leyou.cart.interceptors;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.cart.utils.UserContainer;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 17:22
 * @description TODO
 **/
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    public UserInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = CookieUtils.getCookieValue(request, prop.getCookieName());
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            UserContainer.setUser(user);
            return true;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.FORBBIDEN);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContainer.remove();
    }
}

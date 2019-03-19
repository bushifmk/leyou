package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.user.client.UserClient;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/19 19:36
 * @description TODO
 **/
@Service
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;

    public String login(String username, String password) {
        try {
            User user = userClient.queryByUsernameAndPassword(username, password);
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), prop.getPrivateKey(), prop.getExpire());
            return token;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
    }
}

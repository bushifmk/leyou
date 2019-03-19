package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/19 19:33
 * @description TODO
 **/
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties prop;

    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username, @RequestParam("password") String password,
            HttpServletResponse response, HttpServletRequest request
            ){
        String token= authService.login(username,password);
        //把token写到cookie
        CookieUtils.newBuilder().name(prop.getCookieName()).value(token)
                .request(request).httpOnly(true).response(response).build();
        return ResponseEntity.ok().build();
    }
}

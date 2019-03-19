package com.leyou.user.client;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/19 18:52
 * @description TODO
 **/

import com.leyou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("query")
    User queryByUsernameAndPassword(@RequestParam("username")String username, @RequestParam("password")String password);
}

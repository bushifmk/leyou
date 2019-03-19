package com.leyou.user.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/18 18:56
 * @description TODO
 **/
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data")String data,@PathVariable("type")Integer type){
        return ResponseEntity.ok(userService.checkData(data,type));
    }
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code")String code){
        if(result.hasErrors()){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("query")
    public ResponseEntity<User> queryByUsernameAndPassword(@RequestParam("username")String username,@RequestParam("password")String password){
        return ResponseEntity.ok(userService.queryByUsernameAndPassword(username,password));
    }
}

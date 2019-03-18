package com.leyou.user.service;

import com.leyou.common.constants.RegexConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/18 18:50
 * @description TODO
 **/
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    private final static String KEY_PREFIX="user:verify:phone:";
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        return userMapper.selectCount(user) == 0;
    }

    public void sendCode(String phone) {
        if(!phone.matches(RegexConstants.PHONE_REGEX)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        String code = NumberUtils.generateCode(6);
        redisTemplate.opsForValue().set(KEY_PREFIX+phone, code,5, TimeUnit.MINUTES);
        Map<String,String> msg=new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
    }

    public void register(User user, String code) {
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if(!StringUtils.equals(cacheCode,code)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        String salt = CodecUtils.generateSalt();
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        user.setSalt(salt);
        user.setCreated(new Date());
        int count = userMapper.insert(user);
        if(count!=1){
            throw new LyException(ExceptionEnum.INSERT_DATA_ERROR);
        }
    }
}

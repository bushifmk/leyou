package com.leyou.order.utils;

import com.leyou.auth.pojo.UserInfo;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 18:42
 * @description TODO
 **/
public class UserContainer {
    public static final ThreadLocal<UserInfo> TL=new ThreadLocal<>();

    public static UserInfo getUser() {
        return TL.get();
    }

    public static void setUser(UserInfo user) {
        TL.set(user);
    }
    public static void remove(){
        TL.remove();
    }
}

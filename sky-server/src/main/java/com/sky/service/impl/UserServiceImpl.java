package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //微信登录服务接口地址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
@Autowired
 private WeChatProperties weChatProperties;
@Autowired
private UserMapper userMapper;

//调用微信接口服务，获取微信用户的openid
private String getOpenid(String code) {
    //调用微信接口服务，获取当前微信用户的openid
    Map<String,String> map = new HashMap<>();
    map.put("appid",weChatProperties.getAppid());
    map.put("secret",weChatProperties.getSecret());
    map.put("js_code",code);
    map.put("grant_type", "authorization_code");//这个值是固定的

    String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);//请求地址，请求参数
    JSONObject jsonObject = JSON.parseObject(json);//将字符串转为，json对象
    String openid = jsonObject.getString("openid");
    return openid;
}

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {

        //调用方法，获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空，登录失败，抛出异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        //新用户，自动完成注册
        if(user==null){
          user= User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
          userMapper.insert(user);
        }

        //返回这个用户对象


        return user;
    }



}

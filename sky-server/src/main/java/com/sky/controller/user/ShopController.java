package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")//设置bean的名称，避免冲突
@Slf4j
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

     @Autowired
    private RedisTemplate redisTemplate; //redis

    public static final String KEY="SHOP_STATUS";

    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer o = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态为:{}",o);

        return Result.success(o);
    }


}
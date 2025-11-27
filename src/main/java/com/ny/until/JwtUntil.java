package com.ny.until;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 用户登录相关工具类（已移除Token验证功能）
 */
@Component
public class JwtUntil {

    // 注入Redis操作模板（保留用于存储用户登录状态）
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 从Redis中获取用户数据
    public String getToken(String username) {
        return (String) redisTemplate.opsForValue().get(username);
    }

}
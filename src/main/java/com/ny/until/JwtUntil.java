package com.ny.until;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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

    // 创建JWT Token
    public String createToken(int userId) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }
}
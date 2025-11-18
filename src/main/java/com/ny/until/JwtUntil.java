package com.ny.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUntil {
    // 生成HS256算法的安全密钥
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 注入Redis操作模板
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 根据用户名生成Token（有效期10小时）
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 1000 * 60 * 60 * 10))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 验证Token有效性（签名+过期时间）
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从Token中提取用户名
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 解析Token获取Claims（负载信息）
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    // 将Token存储到Redis（键：用户名，值：Token）
    public void storeToken(String token, String username) {
        redisTemplate.opsForValue().set(username, token);
    }
    
    // 从Redis中获取存储的Token
    public String getToken(String username) {
        return (String) redisTemplate.opsForValue().get(username);
    }
}
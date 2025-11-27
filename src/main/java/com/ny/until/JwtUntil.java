package com.ny.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JwtUntil {
    // 生成HS256算法的安全密钥
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 注入Redis操作模板
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    
    // 是否禁用Redis检查，用于降级方案
    @Value("${TOKEN_REDIS_CHECK_ENABLED:true}")
    private boolean tokenRedisCheckEnabled;
    
    // 本地内存缓存，用于Redis不可用时的降级方案
    private final Map<String, String> tokenLocalCache = new ConcurrentHashMap<>();

    // 根据用户名生成Token（有效期10小时）
    public String generateToken(String username) {
        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + 1000 * 60 * 60 * 10))
                    .signWith(SECRET_KEY)
                    .compact();
            
            // 尝试将Token存储到Redis或本地缓存
            storeToken(token, username);
            return token;
        } catch (Exception e) {
            log.error("Failed to generate token: {}", e.getMessage());
            throw new RuntimeException("Token generation failed", e);
        }
    }

    // 验证Token有效性（签名+过期时间）
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    // 从Token中提取用户名
    public String getUsernameFromToken(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    // 解析Token获取Claims（负载信息）
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    // 检查Redis是否可用
    private boolean isRedisAvailable() {
        try {
            // 如果配置禁用了Redis检查，则返回false
            if (!tokenRedisCheckEnabled) {
                return false;
            }
            
            // 检查Redis连接工厂是否运行
            if (redisConnectionFactory != null) {
                String pingResult = redisConnectionFactory.getConnection().ping();
                return "PONG".equals(pingResult);
            }
            return false;
        } catch (Exception e) {
            log.warn("Redis is not available: {}", e.getMessage());
            return false;
        }
    }

    // 将Token存储到Redis（键：用户名，值：Token）
    public void storeToken(String token, String username) {
        try {
            if (isRedisAvailable()) {
                redisTemplate.opsForValue().set(username, token);
                log.debug("Token stored in Redis for user: {}", username);
                // 从本地缓存中删除，避免数据不一致
                tokenLocalCache.remove(username);
            } else {
                // Redis不可用时，存储到本地内存缓存
                tokenLocalCache.put(username, token);
                log.warn("Redis unavailable, token stored in local cache for user: {}", username);
            }
        } catch (Exception e) {
            // 发生异常时，降级到本地缓存
            tokenLocalCache.put(username, token);
            log.error("Failed to store token: {}", e.getMessage());
        }
    }
    
    // 从Redis或本地缓存中获取存储的Token
    public String getToken(String username) {
        try {
            if (isRedisAvailable()) {
                String token = (String) redisTemplate.opsForValue().get(username);
                if (token != null) {
                    return token;
                }
            }
            
            // Redis不可用或Token不存在时，从本地缓存获取
            String localToken = tokenLocalCache.get(username);
            if (localToken != null) {
                log.debug("Token retrieved from local cache for user: {}", username);
            }
            return localToken;
        } catch (Exception e) {
            log.error("Failed to retrieve token: {}", e.getMessage());
            // 发生异常时，尝试从本地缓存获取
            return tokenLocalCache.get(username);
        }
    }
}
package com.ny.until;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;

@Configuration
@Slf4j
public class RedisUntil {
    
    // 从环境变量读取Redis配置，提供默认值以支持本地开发
    @Value("${REDIS_HOST:localhost}")
    private String redisHost;
    
    @Value("${REDIS_PORT:6379}")
    private int redisPort;
    
    @Value("${REDIS_PASSWORD:}")
    private String redisPassword;
    
    @Value("${REDIS_ENABLED:true}")
    private boolean redisEnabled;

    // 连接Redis数据库
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        if (!redisEnabled) {
            log.warn("Redis is disabled by configuration");
            // 返回一个禁用的连接工厂，避免应用启动失败
            return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379)) {
                @Override
                public void afterPropertiesSet() {
                    // 重写此方法，不进行实际连接
                    log.info("Redis connection factory initialized in disabled mode");
                }
                
                @Override
                public boolean isRunning() {
                    return false;
                }
            };
        }
        
        log.info("Initializing Redis connection to {}:{}", redisHost, redisPort);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        
        // 如果提供了密码，则设置密码
        if (StringUtils.hasText(redisPassword)) {
            config.setPassword(redisPassword);
        }
        
        try {
            LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
            // 设置连接超时时间，避免连接过程阻塞太久
            factory.setTimeout(3000);
            return factory;
        } catch (Exception e) {
            log.error("Failed to create Redis connection factory: {}", e.getMessage());
            // 提供一个备用的连接工厂，避免应用启动失败
            return createFallbackConnectionFactory();
        }
    }
    
    // 创建备用的连接工厂，用于降级方案
    private LettuceConnectionFactory createFallbackConnectionFactory() {
        log.warn("Creating fallback Redis connection factory (disabled mode)");
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379)) {
            @Override
            public void afterPropertiesSet() {
                log.info("Fallback Redis connection factory initialized");
            }
            
            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    // 提供Redis操作模板（与Redis服务器交互）
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        
        // 设置键序列化器
        template.setKeySerializer(new StringRedisSerializer());
        
        try {
            LettuceConnectionFactory factory = redisConnectionFactory();
            template.setConnectionFactory(factory);
            
            // 尝试初始化连接
            try {
                factory.afterPropertiesSet();
                if (factory.isRunning()) {
                    template.afterPropertiesSet();
                    log.info("Redis template initialized successfully");
                } else {
                    log.warn("Redis connection is not available, template initialized in fallback mode");
                }
            } catch (Exception e) {
                log.error("Failed to initialize Redis template: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Critical error configuring Redis template: {}", e.getMessage());
        }
        
        return template;
    }
}
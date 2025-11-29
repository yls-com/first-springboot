// 在 com.ny.config 包下创建 CorsConfig.java
package com.ny.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:8081}")
    private List<String> allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的域,从配置文件读取，避免使用通配符
        config.setAllowedOrigins(allowedOrigins);
        
        // 允许跨域请求携带 cookie
        config.setAllowCredentials(true);
        
        // 允许的请求方法 - 只允许必要的HTTP方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许的请求头 - 限制必要的头信息
        config.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", 
            "X-Requested-With", "X-CSRF-Token"
        ));
        
        // 暴露的头信息 - 只暴露必要的响应头
        config.setExposedHeaders(Arrays.asList(
            "Content-Length", "Content-Type", "Authorization"
        ));
        
        // 配置有效期 - 设置合理的缓存时间
        config.setMaxAge(3600L);
        
        // 映射所有请求路径
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
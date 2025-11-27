// 在 com.ny.config 包下创建 CorsConfig.java
package com.ny.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端域名访问（* 表示允许所有，生产环境建议指定具体域名）
        config.addAllowedOriginPattern("*");
        config.addAllowedOriginPattern("https://yls-com.github.io");
        // 允许跨域请求携带 cookie
        config.setAllowCredentials(true);
        // 允许的请求方法（GET、POST、PUT 等）
        config.addAllowedMethod("*");
        // 允许的请求头（包括自定义头，如 token）
        config.addAllowedHeader("*");
        // 映射所有请求路径
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
package com.ny.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 请求拦截器（验证登录Token，手动放行登录/注册接口）
 * @author MI
 * @date 2023/10/03
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 请求处理前拦截（核心逻辑：先放行登录/注册，再验证其他接口Token）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取当前请求路径（不含参数，如 "/login"）
        String requestUri = request.getRequestURI();
        log.info("拦截到请求：{}", requestUri);

        // 2. 手动放行 /login 和 /register 接口（无需验证Token）
        if ("/login".equals(requestUri) || "/register".equals(requestUri) || "/error".equals(requestUri)) {
            log.info("请求 {} 为放行接口，直接放行", requestUri);
            return true;
        }

        // 其他接口直接放行（已移除Token验证）
        log.info("请求 {} 已放行（Token验证已移除）", requestUri);
        return true;
    }

    // 以下方法为拦截器默认方法，保持空实现即可（无需修改）
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
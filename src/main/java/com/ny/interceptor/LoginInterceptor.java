package com.ny.interceptor;

import com.ny.until.JwtUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求拦截器（验证登录Token）
 * @author MI
 * @date 2023/10/03
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    JwtUntil jwtUtil;

    // 请求处理前拦截（验证Token）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中取出Token
        String token = request.getHeader("token");
        // 验证Token是否存在且合法
        if (token != null && jwtUtil.validateToken(token)) {
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("100,还未登录");
            return false;
        }
    }

    // 以下方法保持空实现
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
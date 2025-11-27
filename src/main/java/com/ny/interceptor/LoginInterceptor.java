package com.ny.interceptor;

import com.ny.until.JwtUntil;
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

    @Resource
    private JwtUntil jwtUtil; // 注入你的JWT工具类（已实现完整逻辑）

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

        // 3. 其他接口：验证Token是否有效
        String token = request.getHeader("token");
        log.info("当前请求头中的Token：{}", token);

        // 验证Token存在且合法（调用你已实现的 jwtUtil.validateToken 方法）
        if (token != null && jwtUtil.validateToken(token)) {
            // 可选：额外验证Redis中是否存在该Token（防止注销后Token仍可用）
            String username = jwtUtil.getUsernameFromToken(token);
            String redisToken = jwtUtil.getToken(username);
            if (token.equals(redisToken)) {
                log.info("Token验证通过（Redis中存在有效Token），放行请求：{}", requestUri);
                return true;
            } else {
                log.warn("Token已失效（Redis中无对应记录），拒绝请求：{}", requestUri);
            }
        } else {
            log.warn("Token无效或未携带Token，拒绝请求：{}", requestUri);
        }

        // Token无效/不存在/已注销：返回401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401状态码
        response.setContentType("text/plain;charset=UTF-8"); // 响应编码（避免中文乱码）
        response.getWriter().write("100,还未登录"); // 自定义响应信息
        return false;
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